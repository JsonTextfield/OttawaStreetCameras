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
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.Filter
import android.widget.ImageView
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.textfield.json.ottawastreetcameras.CameraFilter
import com.textfield.json.ottawastreetcameras.MyLinearLayout
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood
import kotlinx.android.synthetic.main.activity_alternate_main.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class AlternateMainActivity : AppCompatActivity(), OnMapReadyCallback, AbsListView.MultiChoiceModeListener, MyLinearLayout.OnLetterTouchListener {

    private val neighbourhoods = ArrayList<Neighbourhood>()
    private val cameras = ArrayList<Camera>()
    private val selectedCameras = ArrayList<Camera>()
    private val requestForList = 0
    private val requestForMap = 1
    private val prefNameHidden = "hidden"
    private val prefNameFavourites = "favourites"
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
    private lateinit var searchview: MenuItem
    private lateinit var selectAll: MenuItem

    private var mapIsLoaded = false

    private val index = HashMap<Char, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternate_main)

        downloadJson()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sectionIndex.setOnTouchingLetterChangedListener(this)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        sortName = menu.findItem(R.id.sort_name)
        sortDistance = menu.findItem(R.id.distance_sort)
        searchview = menu.findItem(R.id.user_searchView)
        val searchView = searchview.actionView as SearchView

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

                if (newText.isNotEmpty() || sortName.isVisible) {
                    sectionIndex.visibility = View.INVISIBLE
                } else {
                    sectionIndex.visibility = View.VISIBLE
                }
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
                val intent = Intent(this@AlternateMainActivity, CameraActivity::class.java)
                intent.putExtra("cameras", arrayListOf(cameras[Random().nextInt(cameras.size)]))
                startActivityForResult(intent, 0)
            }
            R.id.favourites -> {
                searchview.expandActionView()
                (searchview.actionView as SearchView).setQuery("f: ", false)
            }
            R.id.hidden -> {
                searchview.expandActionView()
                (searchview.actionView as SearchView).setQuery("h: ", false)
            }
        }
        return true
    }

    private fun getNeighbourhoods() {
        val url = "http://data.ottawa.ca/dataset/302ade92-51ec-4b26-a715-627802aa62a8/resource/f1163794-de80-4682-bda5-b13034984087/download/onsboundariesgen1.shp.json"
        val queue = Volley.newRequestQueue(this)
        val jsObjRequest = JsonObjectRequest(url, null, Response.Listener { response ->
            val array = response.getJSONArray("features")
            (0 until array.length())
                    .map { array.get(it) as JSONObject }
                    .forEach {
                        neighbourhoods.add(Neighbourhood(it))
                    }
            val d = Date()

            AsyncTask.execute {
                for (camera in cameras) {
                    for (neighbourhood in neighbourhoods)
                        if (neighbourhood.containsCamera(camera)) {
                            camera.neighbourhood = neighbourhood.getName()
                            break
                        }
                }
                runOnUiThread {
                    println(Date().time - d.time)
                    progress_bar.visibility = View.INVISIBLE
                }
            }

        }, Response.ErrorListener {
            println(it)
        })
        queue.add(jsObjRequest)
    }

    private fun downloadJson() {
        progress_bar.visibility = View.VISIBLE
        val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        val url = "http://traffic.ottawa.ca/map/camera_list"
        val queue = Volley.newRequestQueue(this)
        val jsObjRequest = JsonArrayRequest(url, Response.Listener { response ->
            (0 until response.length())
                    .map { response.get(it) as JSONObject }
                    .forEach {
                        val camera = Camera(it)
                        camera.isFavourite = camera.num.toString() in sharedPrefs.getStringSet(prefNameFavourites, HashSet<String>())
                        camera.setVisibility(camera.num.toString() !in sharedPrefs.getStringSet(prefNameHidden, HashSet<String>()))
                        cameras.add(camera)
                    }

            Collections.sort(cameras, SortByName())
            myAdapter = object : CameraAdapter(this, cameras) {
                override fun complete() {
                    setupSectionIndex()
                }

            }
            listView.adapter = myAdapter
            myAdapter.filter.filter("")
            //map?.getFilter(cameras, mapIsLoaded)?.filter("")


            toolbar.setOnClickListener {
                listView.setSelection(0)
            }

            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                val intent = Intent(this, CameraActivity::class.java)
                intent.putParcelableArrayListExtra("cameras", arrayListOf(myAdapter.getItem(i)))
                startActivityForResult(intent, 0)
            }

            listView.setMultiChoiceModeListener(this)
            setSupportActionBar(toolbar)
            setupSectionIndex()
            loadMarkers()
            progress_bar.visibility = View.INVISIBLE
            //getNeighbourhoods()


        }, Response.ErrorListener {
            print(it)
            showErrorDialogue(this)
        })
        queue.add(jsObjRequest)
    }

    private fun setupSectionIndex() {
        index.clear()
        sectionIndex.removeAllViews()
        //assumes cameras are sorted
        for (i in 0 until myAdapter.count) {

            //get the first character
            val c = myAdapter.getItem(i).getSortableName()[0]
            if (c !in index.keys && myAdapter.getItem(i).isVisible) {
                sectionIndex.add(c.toString())
                index[c] = i
            }
        }
    }

    override fun onLetterTouch(c: Char) {
        listView.setSelection(index[c]!!)
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
                if (actionMode != null) {
                    selectCamera(camera)
                } else {
                    val intent = Intent(this, CameraActivity::class.java)
                    intent.putParcelableArrayListExtra("cameras", arrayListOf(camera))
                    startActivityForResult(intent, 0)
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
        /*data?.let {
            for (camera in cameras) {
                for (cam in it.getParcelableArrayListExtra<Camera>("cameras")) {
                    if (camera.num == cam.num) {
                        camera.isFavourite = cam.isFavourite
                        camera.isVisible = cam.isVisible
                        break
                    }
                }
            }

        }
        cameras.clear()
        downloadJson()*/

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
        } else {
            selectedCameras.add(camera)
            camera.marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        }

        if (selectedCameras.isEmpty()) {
            actionMode!!.finish()
            return false
        }

        val allFav = selectedCameras.map { it.isFavourite }.reduce { acc, b -> acc && b }
        addFav.isVisible = !allFav
        removeFav.isVisible = allFav

        val allInvis = selectedCameras.map { !it.isVisible }.reduce { acc, b -> acc && b }
        hide.isVisible = !allInvis
        unhide.isVisible = allInvis

        showCameras.isVisible = selectedCameras.size <= maxCameras

        selectAll.isVisible = selectedCameras.size < myAdapter.count

        actionMode?.title = resources.getQuantityString(R.plurals.selectedCameras, selectedCameras.size, selectedCameras.size)

        return camera in selectedCameras
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.open_cameras -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putParcelableArrayListExtra("cameras", selectedCameras)
                startActivityForResult(intent, 0)
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

        setupSectionIndex()
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

        myAdapter.filter.filter((searchview.actionView as SearchView).query)
        map?.getFilter(cameras, mapIsLoaded)?.filter((searchview.actionView as SearchView).query)

        actionMode = null
    }

    override fun onItemCheckedStateChanged(actionMode: android.view.ActionMode, i: Int, l: Long, b: Boolean) {
        if (!selectCamera(myAdapter.getItem(i)) && b) {
            listView.setItemChecked(i, false)
        }
    }

}

fun AppCompatActivity.modifyPrefs(pref: String, selectedCameras: Collection<Camera>, willAdd: Boolean) {
    val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
    val prefList = HashSet<String>(sharedPrefs.getStringSet(pref, HashSet<String>()))
    val editor = sharedPrefs.edit()

    if (willAdd) {
        prefList.addAll(selectedCameras.map { it.num.toString() })
    } else {
        prefList.removeAll(selectedCameras.map { it.num.toString() })
    }
    editor.putStringSet(pref, prefList).apply()
}

fun GoogleMap.getFilter(cameras: ArrayList<Camera>, mapIsLoaded: Boolean): Filter {
    return object : CameraFilter(cameras) {
        override fun refresh(list: ArrayList<Camera>) {
            val latLngBounds = LatLngBounds.Builder()
            var anyVisible = false

            for (camera in cameras) {
                camera.setVisibility(camera in list)

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