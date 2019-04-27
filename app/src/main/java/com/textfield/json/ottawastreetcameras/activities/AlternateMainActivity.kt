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
import android.widget.*
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
import com.textfield.json.ottawastreetcameras.CameraFilter
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
    private var cameras: List<Camera> = ArrayList()
    private val selectedCameras = ArrayList<Camera>()
    private val requestForList = 0
    private val requestForMap = 1
    private val permissionArray = arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
    private val maxCameras = 4

    private lateinit var myAdapter: CameraAdapter
    private lateinit var locationManager: LocationManager

    private var map: GoogleMap? = null
    private var actionMode: ActionMode? = null

    private lateinit var sortName: MenuItem
    private lateinit var sortDistance: MenuItem
    private lateinit var showCameras: MenuItem
    private lateinit var addFav: MenuItem
    private lateinit var removeFav: MenuItem
    private lateinit var hide: MenuItem
    private lateinit var unhide: MenuItem
    private lateinit var searchMenuItem: MenuItem
    private lateinit var selectAll: MenuItem

    private var mapIsLoaded = false
    private lateinit var listView: ListView
    private lateinit var sectionIndex: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternate_main)
        listView = section_index_listview.listview
        sectionIndex = section_index_listview.sectionindex
        downloadJson()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        sortName = menu.findItem(R.id.sort_name)
        sortDistance = menu.findItem(R.id.distance_sort)
        searchMenuItem = menu.findItem(R.id.user_searchView)
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
        }
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
        loadMarkers()
        getNeighbourhoods()
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
        map = googleMap
        requestPermissions(requestForMap)
        googleMap.setOnMapLoadedCallback { loadMarkers() }
    }

    private fun loadMarkers() {
        map?.let { map ->
            map.setOnInfoWindowLongClickListener { marker ->
                if (actionMode == null) {
                    actionMode = startActionMode(this)
                }
                selectCamera(marker.tag as Camera)
            }
            map.setOnInfoWindowClickListener { marker ->
                val camera = marker.tag as Camera
                if (selectCamera(camera) && actionMode == null) {
                    showSelectedCameras()
                }
            }
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
                mapIsLoaded = true
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
            }
        }
        //map?.getFilter(cameras, mapIsLoaded)?.filter("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (actionMode == null && selectedCameras.isNotEmpty()) {
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

    private fun selectCamera(camera: Camera): Boolean {
        if (camera in selectedCameras) {
            selectedCameras.remove(camera)
            camera.marker?.setIcon(BitmapDescriptorFactory.defaultMarker())
            if (camera.isFavourite) {
                camera.marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            }
            if (selectedCameras.isEmpty()) {
                actionMode!!.finish()
                return false
            }
        } else {
            selectedCameras.add(camera)
            camera.marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        }
        actionMode?.let { actionMode ->
            actionMode.title = resources.getQuantityString(R.plurals.selectedCameras, selectedCameras.size, selectedCameras.size)

            val allFav = selectedCameras.map { it.isFavourite }.reduce { acc, b -> acc && b }
            addFav.isVisible = !allFav
            removeFav.isVisible = allFav

            val allInvis = selectedCameras.map { !it.isVisible }.reduce { acc, b -> acc && b }
            hide.isVisible = !allInvis
            unhide.isVisible = allInvis

            showCameras.isVisible = selectedCameras.size <= maxCameras
            selectAll.isVisible = selectedCameras.size < myAdapter.count
        }
        return camera in selectedCameras
    }

    private fun showSelectedCameras() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putParcelableArrayListExtra("cameras", selectedCameras)
        startActivityForResult(intent, 0)
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.open_cameras -> {
                showSelectedCameras()
                return true
            }
            R.id.add_favourites -> {
                addRemoveFavs(true)
                return true
            }
            R.id.remove_favourite -> {
                addRemoveFavs(false)
                return true
            }
            R.id.hide -> {
                showOrHide(true)
                return true
            }
            R.id.unhide -> {
                showOrHide(false)
                return true
            }
            R.id.select_all -> {
                selectedCameras.clear()
                for (i in 0 until myAdapter.count) {
                    listView.setItemChecked(i, true)
                }
                return true
            }
            else -> return false
        }
    }

    private fun addRemoveFavs(willAdd: Boolean) {
        modifyPrefs(prefNameFavourites, selectedCameras, willAdd)
        cameras.filter { it in selectedCameras }.forEach { it.isFavourite = willAdd }

        for (camera in cameras) {
            if (camera.isFavourite) {
                camera.marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            } else {
                camera.marker?.setIcon(BitmapDescriptorFactory.defaultMarker())
            }
        }
        for (i in 0 until myAdapter.count) {
            if (listView.checkedItemPositions[i]) {
                val view = listView.getViewByPosition(i)
                val starImageView = view.findViewById<ImageView>(R.id.star)
                starImageView.setImageDrawable(if (willAdd) {
                    ContextCompat.getDrawable(this, R.drawable.outline_star_white_18)
                } else {
                    ContextCompat.getDrawable(this, R.drawable.outline_star_border_white_18)
                })
            }
        }

        addFav.isVisible = !willAdd
        removeFav.isVisible = willAdd
    }

    private fun showOrHide(willHide: Boolean) {
        modifyPrefs(prefNameHidden, selectedCameras, willHide)
        cameras.filter { it in selectedCameras }.forEach { it.setVisibility(!willHide) }

        hide.isVisible = !willHide
        unhide.isVisible = willHide

        section_index_listview.updateIndex()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        actionMode = mode
        actionMode!!.menuInflater.inflate(R.menu.contextual_menu, menu)

        selectAll = menu!!.findItem(R.id.select_all)
        showCameras = menu.findItem(R.id.open_cameras)
        removeFav = menu.findItem(R.id.remove_favourite)
        addFav = menu.findItem(R.id.add_favourites)
        unhide = menu.findItem(R.id.unhide)
        hide = menu.findItem(R.id.hide)

        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        cameras.filter { it in selectedCameras }.forEach { selectCamera(it) }
        myAdapter.filter.filter((searchMenuItem.actionView as SearchView).query)
        map?.getFilter(cameras, mapIsLoaded)?.filter((searchMenuItem.actionView as SearchView).query)
        actionMode = null
    }

    override fun onItemCheckedStateChanged(actionMode: ActionMode, i: Int, l: Long, b: Boolean) {
        if (!selectCamera(myAdapter.getItem(i)) && b) {
            listView.setItemChecked(i, false)
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