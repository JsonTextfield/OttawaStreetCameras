package com.textfield.json.ottawastreetcameras.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.textfield.json.ottawastreetcameras.*
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
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
    private val markers = ArrayList<Marker>()
    private val requestForList = 0
    private val requestForMap = 1
    private val permissionArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val maxCameras = 4

    private lateinit var myAdapter: CameraAdapter
    private lateinit var locationManager: LocationManager

    private var map: GoogleMap? = null
    private var actionMode: android.view.ActionMode? = null

    private lateinit var sortName: MenuItem
    private lateinit var sortDistance: MenuItem
    private lateinit var showCameras: MenuItem
    private lateinit var addFav: MenuItem
    private lateinit var removeFav: MenuItem
    private lateinit var hide: MenuItem
    private lateinit var unhide: MenuItem
    private lateinit var searchview: MenuItem

    private val index = HashMap<Char, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternate_main)

        downloadJson()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sectionIndex.setOnTouchingLetterChangedListener(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        sortName = menu.findItem(R.id.sort_name)
        sortDistance = menu.findItem(R.id.distance_sort)
        searchview = menu.findItem(R.id.user_searchView)
        val searchView = menu.findItem(R.id.user_searchView).actionView as SearchView

        searchView.queryHint = String.format(resources.getString(R.string.search_hint), cameras.size)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                for (marker in markers) {
                    val camera = marker.tag as Camera
                    when {
                        newText.startsWith("f: ") -> marker.isVisible =
                                camera.isFavourite && camera.getName().contains(newText.removePrefix("f: "), true)

                        newText.startsWith("h: ") -> marker.isVisible =
                                !camera.isVisible && camera.getName().contains(newText.removePrefix("h: "), true)

                        newText.startsWith("n: ") -> marker.isVisible =
                                camera.neighbourhood.contains(newText.removePrefix("n: "), true)

                        else -> marker.isVisible = camera.getName().contains(newText, true) && camera.isVisible
                    }
                }
                myAdapter.filter.filter(newText)
                if (newText.isEmpty() && sortDistance.isVisible) {
                    sectionIndex.visibility = View.VISIBLE
                } else {
                    sectionIndex.visibility = View.INVISIBLE
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
                intent.putExtra("cameras", ArrayList(Arrays.asList(cameras[Random().nextInt(cameras.size)])))
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
                    .forEach { neighbourhoods.add(Neighbourhood(it)) }

            AsyncTask.execute({
                for (camera in cameras) {
                    for (neighbourhood in neighbourhoods) {
                        if (neighbourhood.containsCamera(camera)) {
                            camera.neighbourhood = neighbourhood.getName()
                            break
                        }
                    }
                }

                runOnUiThread({
                    for (camera in cameras) {
                        if (camera.neighbourhood == "") {
                            println(camera)
                        }
                    }
                })
            })
        }, Response.ErrorListener {
            println(it)
        })
        queue.add(jsObjRequest)
    }

    private fun downloadJson() {
        progress_bar.visibility = View.VISIBLE
        val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        val url = "https://traffic.ottawa.ca/map/camera_list"
        val queue = Volley.newRequestQueue(this)
        val jsObjRequest = JsonArrayRequest(url, Response.Listener { response ->
            (0 until response.length())
                    .map { response.get(it) as JSONObject }
                    .forEach {
                        val camera = Camera(it)
                        camera.isFavourite = sharedPrefs.getStringSet("favourites", HashSet<String>()).contains(camera.num.toString())
                        camera.isVisible = !sharedPrefs.getStringSet("hidden", HashSet<String>()).contains(camera.num.toString())
                        cameras.add(camera)
                    }

            Collections.sort(cameras, SortByName())
            myAdapter = CameraAdapter(this, cameras)
            listView.adapter = myAdapter
            myAdapter.filter.filter("")

            toolbar.setOnClickListener {
                listView.setSelection(0)
            }

            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                val intent = Intent(this, CameraActivity::class.java)
                intent.putParcelableArrayListExtra("cameras", ArrayList(Arrays.asList(myAdapter.getItem(i))))
                startActivityForResult(intent, 0)
            }

            listView.setMultiChoiceModeListener(this)
            setSupportActionBar(toolbar)
            setupSectionIndex()
            loadMarkers()
            getNeighbourhoods()
            progress_bar.visibility = View.INVISIBLE

        }, Response.ErrorListener {
            showErrorDialogue(this)
        })
        queue.add(jsObjRequest)
    }

    private fun setupSectionIndex() {

        //assumes cameras are sorted
        for (i in cameras.indices) {

            //get the first character
            val c = cameras[i].getName().replace(Regex("\\W"), "")[0]
            if (c !in index.keys) {
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
    }

    private fun refreshMarkers() {
        for (marker in markers) {
            val camera = marker.tag as Camera
            if (camera.isFavourite) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            } else {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker())
            }
            marker.isVisible = camera.isVisible
        }
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

    private fun loadMarkers() {
        map?.let { map ->
            map.setOnInfoWindowLongClickListener { marker ->
                if (actionMode == null) {
                    actionMode = startActionMode(this)
                    selectedCameras.clear()
                }
                selectMarker(marker, selectCamera(marker.tag as Camera))
            }
            map.setOnInfoWindowClickListener { marker ->
                val camera = marker.tag as Camera
                if (actionMode != null) {
                    selectMarker(marker, selectCamera(camera))
                    if (selectedCameras.isEmpty()) {
                        actionMode!!.finish()
                    }
                } else {
                    val intent = Intent(this, CameraActivity::class.java)
                    intent.putParcelableArrayListExtra("cameras", ArrayList(Arrays.asList(camera)))
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
                markers.add(m)
                builder.include(m.position)

                m.isVisible = camera.isVisible
            }

            map.setLatLngBoundsForCameraTarget(builder.build())
            map.setOnMapLoadedCallback {
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50))
            }
        }
    }

    private fun requestPermissions(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permissionArray[0]) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permissionArray[1]) != PackageManager.PERMISSION_GRANTED) {
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
        } else {
            selectedCameras.add(camera)
        }

        if (selectedCameras.filter({ it.isFavourite }).size == selectedCameras.size) {
            addFav.isVisible = false
            removeFav.isVisible = true
        } else {
            addFav.isVisible = true
            removeFav.isVisible = false
        }

        if (selectedCameras.filter({ !it.isVisible }).size == selectedCameras.size) {
            hide.isVisible = false
            unhide.isVisible = true
        } else {
            hide.isVisible = true
            unhide.isVisible = false
        }

        showCameras.isVisible = selectedCameras.size <= maxCameras

        if (!selectedCameras.isEmpty()) {
            actionMode?.title = String.format(resources.getQuantityString(R.plurals.selectedCameras, selectedCameras.size), selectedCameras.size)
        }
        return camera in selectedCameras
    }

    private fun selectMarker(marker: Marker, boolean: Boolean) {
        if (boolean) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        } else {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker())
        }
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
            else -> return false
        }
    }

    private fun addRemoveFavs(willAdd: Boolean) {
        modifyPrefs("favourites", selectedCameras, willAdd)
        cameras.filter { it in selectedCameras }.forEach { it.isFavourite = willAdd }
        for (marker in markers) {
            if ((marker.tag as Camera).isFavourite) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            } else {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker())
            }
        }

        addFav.isVisible = !willAdd
        removeFav.isVisible = willAdd
    }

    private fun showOrHide(willHide: Boolean) {
        modifyPrefs("hidden", selectedCameras, willHide)
        cameras.filter { it in selectedCameras }.forEach { it.isVisible = !willHide }
        markers.forEach { it.isVisible = (it.tag as Camera).isVisible }

        hide.isVisible = !willHide
        unhide.isVisible = willHide
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        selectedCameras.clear()
        actionMode = mode
        actionMode!!.menuInflater.inflate(R.menu.contextual_menu, menu)
        showCameras = menu!!.findItem(R.id.open_cameras)
        removeFav = menu.findItem(R.id.remove_favourite)
        removeFav.isVisible = false
        addFav = menu.findItem(R.id.add_favourites)
        unhide = menu.findItem(R.id.unhide)
        unhide.isVisible = false
        hide = menu.findItem(R.id.hide)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        markers.filter { it.tag in selectedCameras }.forEach { selectMarker(it, false) }
        selectedCameras.clear()
        actionMode = null
    }

    override fun onItemCheckedStateChanged(actionMode: android.view.ActionMode, i: Int, l: Long, b: Boolean) {
        if (!selectCamera(myAdapter.getItem(i)) && b) {
            listView.setItemChecked(i, false)
        }
    }

}

fun AppCompatActivity.showErrorDialogue(context: Context) {
    val builder = AlertDialog.Builder(context)

    builder.setTitle(resources.getString(R.string.no_network_title))
            .setMessage(resources.getString(R.string.no_network_content))
            .setPositiveButton("OK") { _, _ -> finish() }
            .setOnDismissListener { finish() }
    val dialog = builder.create()
    dialog.show()
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