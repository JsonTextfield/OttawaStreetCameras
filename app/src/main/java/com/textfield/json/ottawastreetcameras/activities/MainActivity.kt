package com.textfield.json.ottawastreetcameras.activities

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Filter
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.JsonArrayRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsRequestQueue
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
import com.textfield.json.ottawastreetcameras.adapters.filters.CameraFilter
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashSet

class AlternateMainActivity : GenericActivity(), OnMapReadyCallback {

    private val requestForList = 0
    private val requestForMap = 1
    private val maxCameras = 4

    private lateinit var myAdapter: CameraAdapter

    private var map: GoogleMap? = null

    private var mapIsLoaded = false
    private var searchView: SearchView? = null
    private var searchMenuItem: MenuItem? = null
    private var sortName: MenuItem? = null
    private var sortDistance: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        section_index_listview.defaultTextColour = if (isNightModeOn()) Color.WHITE else Color.BLACK
        progress_bar.visibility = View.VISIBLE

        listView = section_index_listview.listview
        myAdapter = object : CameraAdapter(this, cameras) {
            override fun onComplete() {
                section_index_listview.updateIndex()
            }
        }
        listView.adapter = myAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            selectCamera(myAdapter.getItem(i))
            showSelectedCameras()
        }
        listView.setMultiChoiceModeListener(this)
        toolbar.setOnClickListener {
            listView.setSelection(0)
        }
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            downloadJson()
        } else {
            cameras = savedInstanceState.getParcelableArrayList("cameras") ?: cameras
            loadList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        sortName = menu.findItem(R.id.sort_name)
        sortDistance = menu.findItem(R.id.sort_distance)
        //sortNeighbourhood = menu.findItem(R.id.sort_neighbourhood)
        searchMenuItem = menu.findItem(R.id.user_searchView)
        val nightMode = menu.findItem(R.id.night_mode)
        nightMode.isChecked = isNightModeOn()
        searchView = searchMenuItem?.actionView as SearchView?

        searchView?.queryHint = resources.getQuantityString(R.plurals.search_hint, cameras.size, cameras.size)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView?.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                map?.getFilter(cameras, mapIsLoaded)?.filter(newText)
                //searchView.suggestionsAdapter = NeighbourhoodAdapter(this@AlternateMainActivity, neighbourhoods)

                myAdapter.filter.filter(newText)
                section_index_listview.sectionindex.visibility =
                        if (newText.isNotEmpty() || sortName?.isVisible!!)
                            View.INVISIBLE
                        else
                            View.VISIBLE
                return true
            }
        })
        for (i in 0 until menu.size()) {
            menu.getItem(i)?.icon?.setColorFilter(ContextCompat.getColor(this,
                    if (isNightModeOn()) android.R.color.white else android.R.color.black),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItemMap -> {
                view_switcher.showNext()
            }
            R.id.sort_name -> {
                myAdapter.sort(SortByName())

                section_index_listview.sectionindex.visibility = View.VISIBLE
                sortDistance?.isVisible = true
                //sortNeighbourhood?.isVisible = true
                sortName?.isVisible = false
            }
            R.id.sort_distance -> {
                requestPermissions(requestForList)
            }
            /*R.id.sort_neighbourhood -> {
                myAdapter.sort(SortByNeighbourhood())

                sortNeighbourhood?.isVisible = false
                sortDistance?.isVisible = true
                sortName?.isVisible = true
            }*/
            R.id.random_camera -> {
                selectCamera(cameras[Random().nextInt(cameras.size)])
                showSelectedCameras()
            }
            R.id.shuffle -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putParcelableArrayListExtra("cameras", cameras)
                intent.putExtra("shuffle", true)
                startActivityForResult(intent, 0)
            }
            R.id.favourites -> {
                searchMenuItem?.expandActionView()
                searchView?.setQuery("f: ", false)
            }
            R.id.hidden -> {
                searchMenuItem?.expandActionView()
                searchView?.setQuery("h: ", false)
            }
            R.id.night_mode -> {
                item.isChecked = !item.isChecked
                setNightModeOn(item.isChecked)
                Handler().postDelayed({
                    startActivity(Intent(this, AlternateMainActivity::class.java))
                    finish()
                }, 500)
            }
        }
        return true
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        super.onCreateActionMode(mode, menu)
        saveImage.isVisible = false
        return true
    }

    private fun downloadJson() {
        val jsObjRequest = JsonArrayRequest("https://traffic.ottawa.ca/map/camera_list", { response ->
            val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
            cameras = (0 until response.length()).map {
                val camera = Camera(response.getJSONObject(it))
                camera.setFavourite(camera.num.toString() in sharedPrefs.getStringSet(prefNameFavourites, HashSet<String>())!!)
                camera.setVisible(camera.num.toString() !in sharedPrefs.getStringSet(prefNameHidden, HashSet<String>())!!)
                camera
            } as ArrayList<Camera>
            loadList()
        }, {
            it.printStackTrace()
            showErrorDialogue(this)
        })
        StreetCamsRequestQueue.getInstance(this).add(jsObjRequest)
    }

    private fun loadList() {
        Collections.sort(cameras, SortByName())
        myAdapter.addAll(cameras)
        searchView?.queryHint = resources.getQuantityString(R.plurals.search_hint, cameras.size, cameras.size)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        section_index_listview.updateIndex()
        progress_bar.visibility = View.INVISIBLE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setOnInfoWindowLongClickListener { marker ->
            actionMode = actionMode ?: startActionMode(this)
            selectCamera(marker.tag as Camera)
        }
        googleMap.setOnInfoWindowClickListener { marker ->
            if (selectCamera(marker.tag as Camera) && actionMode == null) {
                showSelectedCameras()
            }
        }
        map = googleMap
        loadMarkers()
        requestPermissions(requestForMap)
    }

    private fun loadMarkers() {
        map?.let { map ->
            val builder = LatLngBounds.Builder()

            //add a marker for every camera available
            for (camera in cameras) {
                val m = map.addMarker(MarkerOptions()
                        .position(LatLng(camera.lat, camera.lng))
                        .title(camera.getName()))
                if (camera.isFavourite) {
                    m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                } else {
                    m.setIcon(BitmapDescriptorFactory.defaultMarker())
                }
                m.tag = camera
                camera.marker = m
                builder.include(m.position)

                m.isVisible = camera.isVisible
            }
            val bounds = builder.build()
            map.setLatLngBoundsForCameraTarget(bounds)
            map.setOnMapLoadedCallback {
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
                mapIsLoaded = true
            }
        }
        //map?.getFilter(cameras, mapIsLoaded)?.filter("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (actionMode == null) {
            selectedCameras.clear()
        }
    }

    private fun requestPermissions(requestCode: Int) {
        val permissionArray = arrayOf(
                permission.ACCESS_FINE_LOCATION,
                permission.ACCESS_COARSE_LOCATION
        )
        val allTrue = permissionArray
                .map { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
                .reduce { acc, b -> acc && b }

        if (allTrue) {
            ActivityCompat.requestPermissions(this, permissionArray, requestCode)
        } else {
            when (requestCode) {
                requestForList -> {
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    myAdapter.sort(SortByDistance(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!))
                    section_index_listview.sectionindex.visibility = View.INVISIBLE
                    sortDistance?.isVisible = false
                    sortName?.isVisible = true
                    //sortNeighbourhood?.isVisible = true
                }
                requestForMap -> map?.isMyLocationEnabled = true
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            if (requestCode == requestForList) {
                requestPermissions(requestForList)
            } else { //if (requestCode == requestForMap) {
                requestPermissions(requestForMap)
            }
        }
    }

    public override fun selectCamera(camera: Camera): Boolean {
        val result = super.selectCamera(camera)
        actionMode?.let {
            showCameras.isVisible = selectedCameras.size <= maxCameras
        }
        return result
    }

    private fun showSelectedCameras() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putParcelableArrayListExtra("cameras", selectedCameras)
        startActivityForResult(intent, 0)
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (!super.onActionItemClicked(mode, item)) {
            return when (item.itemId) {
                R.id.open_cameras -> {
                    showSelectedCameras()
                    true
                }
                else -> false
            }
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        super.onDestroyActionMode(mode)
        if (searchMenuItem != null) {
            myAdapter.filter.filter((searchMenuItem!!.actionView as SearchView).query)
            map?.getFilter(cameras, mapIsLoaded)?.filter((searchMenuItem!!.actionView as SearchView).query)
        }
    }
}

fun GoogleMap.getFilter(cameras: List<Camera>, mapIsLoaded: Boolean): Filter {
    return object : CameraFilter(cameras) {
        override fun onPublishResults(list: ArrayList<Camera>) {
            val latLngBounds = LatLngBounds.Builder()
            var anyVisible = false

            for (camera in cameras) {
                camera.marker?.isVisible = camera in list
                if (camera.isVisible && mapIsLoaded) {
                    latLngBounds.include(camera.marker?.position)
                    anyVisible = true
                }
            }
            if (anyVisible && mapIsLoaded) {
                animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 50))
            }
        }
    }
}