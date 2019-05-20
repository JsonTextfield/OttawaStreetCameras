package com.textfield.json.ottawastreetcameras.activities

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.Filter
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.textfield.json.ottawastreetcameras.adapters.filters.CameraFilter
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsRequestQueue
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood
import kotlinx.android.synthetic.main.activity_alternate_main.*
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class AlternateMainActivity : GenericActivity(), OnMapReadyCallback, AbsListView.MultiChoiceModeListener {

    private var neighbourhoods: List<Neighbourhood> = ArrayList()
    private val requestForList = 0
    private val requestForMap = 1
    private val permissionArray = arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
    val maxCameras = 4

    private lateinit var myAdapter: CameraAdapter
    private lateinit var locationManager: LocationManager

    private var map: GoogleMap? = null
    private var actionMode: ActionMode? = null

    private var mapIsLoaded = false
    private lateinit var sectionIndex: View

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isNightModeOn()) {
            setTheme(R.style.AppTheme)
        } else {
            setTheme(R.style.AppTheme_Light)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternate_main)
        listView = section_index_listview.listview
        sectionIndex = section_index_listview.sectionindex
        downloadJson()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        sortName = menu.findItem(R.id.sort_name)
        sortDistance = menu.findItem(R.id.distance_sort)
        searchMenuItem = menu.findItem(R.id.user_searchView)
        val nightMode = menu.findItem(R.id.night_mode)
        nightMode.isChecked = isNightModeOn()
        val searchView = searchMenuItem.actionView as SearchView

        searchView.queryHint = String.format(resources.getString(R.string.search_hint), cameras.size)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                map?.getFilter(cameras, mapIsLoaded)?.filter(newText)
                //searchView.suggestionsAdapter = NeighbourhoodAdapter(this@AlternateMainActivity, neighbourhoods)

                myAdapter.filter.filter(newText)
                sectionIndex.visibility = if (newText.isNotEmpty() || sortName.isVisible) View.INVISIBLE else View.VISIBLE
                return true
            }
        })
        for (i in 0 until menu.size()) {
            if (isNightModeOn()) {
                menu.getItem(i)?.icon?.setColorFilter(ContextCompat.getColor(this,
                        android.R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                menu.getItem(i)?.icon?.setColorFilter(ContextCompat.getColor(this,
                        android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            }
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

                sectionIndex.visibility = View.VISIBLE
                sortDistance.isVisible = true
                sortName.isVisible = false
            }
            R.id.distance_sort -> {
                requestPermissions(requestForList)
            }
            R.id.random_camera -> {
                selectCamera(cameras[Random().nextInt(cameras.size)])
                showSelectedCameras()
            }
            R.id.favourites -> {
                searchMenuItem.expandActionView()
                (searchMenuItem.actionView as SearchView).setQuery("f: ", false)
            }
            R.id.hidden -> {
                searchMenuItem.expandActionView()
                (searchMenuItem.actionView as SearchView).setQuery("h: ", false)
            }
            R.id.night_mode -> {
                item.isChecked = !item.isChecked
                setNightModeOn(item.isChecked)
                startActivity(Intent(this, AlternateMainActivity::class.java))
                finish()
            }
        }
        return true
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        super.onCreateActionMode(mode, menu)
        saveImage.isVisible = false
        return true
    }

    private fun getNeighbourhoods() {
        val url = "http://data.ottawa.ca/dataset/302ade92-51ec-4b26-a715-627802aa62a8/resource/f1163794-de80-4682-bda5-b13034984087/download/onsboundariesgen1.shp.json"
        val jsObjRequest = JsonObjectRequest(url, null, Response.Listener { response ->
            openFileOutput("neighbourhoods.json", Context.MODE_PRIVATE).use {
                it.write(response.toString().toByteArray())
            }
            processNeighbourhoods()
        }, Response.ErrorListener {
            it.printStackTrace()
        })
        if (!(File(filesDir, "neighbourhoods.json").exists())) {
            StreetCamsRequestQueue.getInstance(this).addHttp(jsObjRequest)
        } else {
            processNeighbourhoods()
        }
    }

    private fun processNeighbourhoods() {
        val file = File(filesDir, "neighbourhoods.json")
        val byteArray = ByteArray(file.length().toInt())
        openFileInput("neighbourhoods.json").read(byteArray)
        val jsonObject = JSONObject(String(byteArray))
        val array = jsonObject.getJSONArray("features")
        neighbourhoods = (0 until array.length()).map { Neighbourhood(array.getJSONObject(it)) }
        AsyncTask.execute {
            for (camera in cameras)
                for (neighbourhood in neighbourhoods)
                    if (neighbourhood.containsCamera(camera)) {
                        camera.neighbourhood = neighbourhood.getName()
                        break
                    }
            runOnUiThread {
                progress_bar.visibility = View.INVISIBLE
            }
        }
    }

    private fun setup() {
        Collections.sort(cameras, SortByName())
        myAdapter = object : CameraAdapter(this, cameras) {
            override fun onComplete() {
                section_index_listview.updateIndex()
            }
        }
        listView.adapter = myAdapter
        myAdapter.filter.filter("")
        map?.getFilter(cameras, mapIsLoaded)?.filter("")

        toolbar.setOnClickListener {
            listView.setSelection(0)
        }
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            selectCamera(myAdapter.getItem(i))
            showSelectedCameras()
        }
        listView.setMultiChoiceModeListener(this)
        setSupportActionBar(toolbar)
        section_index_listview.updateIndex()
        getNeighbourhoods()
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    private fun downloadJson() {
        progress_bar.visibility = View.VISIBLE
        val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        val url = "https://traffic.ottawa.ca/map/camera_list"
        val jsObjRequest = JsonArrayRequest(url, Response.Listener { response ->
            cameras = (0 until response.length())
                    .map {
                        val camera = Camera(response.getJSONObject(it))
                        camera.isFavourite = camera.num.toString() in sharedPrefs.getStringSet(prefNameFavourites, HashSet<String>())!!
                        camera.setVisibility(camera.num.toString() !in sharedPrefs.getStringSet(prefNameHidden, HashSet<String>())!!)
                        camera
                    }
            setup()
            progress_bar.visibility = View.INVISIBLE
        }, Response.ErrorListener {
            it.printStackTrace()
            showErrorDialogue(this)
        })
        StreetCamsRequestQueue.getInstance(this).add(jsObjRequest)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setOnInfoWindowLongClickListener { marker ->
            if (actionMode == null) {
                actionMode = startActionMode(this)
            }
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
        if (actionMode == null) {
            selectedCameras.clear()
        }
    }

    private fun requestPermissions(requestCode: Int) {
        val allTrue = permissionArray
                .map { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
                .reduce { acc, b -> acc && b }

        if (allTrue) {
            ActivityCompat.requestPermissions(this, permissionArray, requestCode)
        } else {
            when (requestCode) {
                requestForList -> {
                    myAdapter.sort(SortByDistance(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)))
                    sectionIndex.visibility = View.INVISIBLE
                    sortDistance.isVisible = false
                    sortName.isVisible = true
                }
                requestForMap ->
                    map?.isMyLocationEnabled = true
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
        myAdapter.filter.filter((searchMenuItem.actionView as SearchView).query)
        map?.getFilter(cameras, mapIsLoaded)?.filter((searchMenuItem.actionView as SearchView).query)
        super.onDestroyActionMode(mode)
        actionMode = null
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