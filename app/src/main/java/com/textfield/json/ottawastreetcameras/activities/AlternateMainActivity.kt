package com.textfield.json.ottawastreetcameras.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
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

class AlternateMainActivity : AppCompatActivity(), OnMapReadyCallback, AbsListView.MultiChoiceModeListener, MyLinearLayout.OnTouchingLetterChangedListener {

    private val cameras = ArrayList<Camera>()
    private val selectedCameras = ArrayList<Camera>()
    private val markers = ArrayList<Marker>()
    private val requestForList = 0
    private val requestForMap = 1
    private val permissionArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val maxCameras = 4

    private lateinit var viewSwitcher: ViewSwitcher
    private lateinit var myAdapter: CameraAdapter
    private lateinit var locationManager: LocationManager

    private var map: GoogleMap? = null
    private var actionMode: android.view.ActionMode? = null

    private var sortName: MenuItem? = null
    private var sortDistance: MenuItem? = null
    private var showCameras: MenuItem? = null
    private var addFav: MenuItem? = null
    private var removeFav: MenuItem? = null

    private lateinit var searchview: SearchView

    private val index = HashMap<Char, Int>()
    private lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternate_main)

        sharedPrefs = this.getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        viewSwitcher = findViewById<ViewSwitcher>(R.id.view_switcher)
        sectionIndex.setOnTouchingLetterChangedListener(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        downloadJson()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        sortName = menu.findItem(R.id.sort_name)
        sortDistance = menu.findItem(R.id.distance_sort)
        val searchView = menu.findItem(R.id.user_searchView).actionView as SearchView
        searchView.queryHint = String.format(resources.getString(R.string.search_hint), cameras.size)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                for (marker in markers) {
                    marker.isVisible = (marker.tag as Camera).getName().contains(newText, true)
                }
                myAdapter.filter.filter(newText)
                if (newText.isEmpty() && sortDistance!!.isVisible) {
                    sectionIndex.visibility = View.VISIBLE
                } else {
                    sectionIndex.visibility = View.INVISIBLE
                }
                return true
            }
        })

        searchview = searchView
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItemMap -> {
                selectedCameras.clear()
                viewSwitcher.showNext()
            }
            R.id.sort_name -> {
                myAdapter.sort(SortByName())

                sectionIndex.visibility = View.VISIBLE
                sortDistance?.isVisible = true
                sortName?.isVisible = false
            }
            R.id.distance_sort -> {
                requestPermissions(requestForList)
            }
            R.id.random_camera -> {
                val intent = Intent(this@AlternateMainActivity, CameraActivity::class.java)
                intent.putExtra("cameras", ArrayList(Arrays.asList(cameras[Random().nextInt(cameras.size)])))
                startActivity(intent)
            }
            R.id.favourites -> {
                showFavourites()
            }
        }
        return true
    }

    private fun showFavourites() {
        searchview.setQuery("*favourites*", false)
    }

    private fun downloadJson() {

        val url = "https://traffic.ottawa.ca/map/camera_list"
        val queue = Volley.newRequestQueue(this)
        val jsObjRequest = JsonArrayRequest(url, Response.Listener { response ->
            (0 until response.length())
                    .map { response.get(it) as JSONObject }
                    .forEach { cameras.add(Camera(it)) }

            for (camera in cameras) {
                camera.isFavourite = (sharedPrefs.getStringSet("favourites", HashSet<String>()).contains(camera.num.toString()))
            }

            Collections.sort(cameras, SortByName())
            myAdapter = CameraAdapter(this, cameras)
            listView.adapter = myAdapter

            toolbar.setOnClickListener { listView.setSelection(0) }
            setSupportActionBar(toolbar)

            setupSectionIndex()
            setupListView()
            loadMarkers()
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

            if (!index.containsKey(c)) {
                val t = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)

                t.layoutParams = layoutParams
                t.text = c.toString()
                t.textSize = 10f
                t.setTextColor(Color.WHITE)
                t.gravity = Gravity.CENTER
                sectionIndex.addView(t)
                index[c] = i

            }

        }
    }

    override fun onTouchingLetterChanged(c: Char) {
        listView.setSelection(index[c]!!)
    }

    private fun setupListView() {
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->

            val intent = Intent(this, CameraActivity::class.java)
            intent.putParcelableArrayListExtra("cameras", ArrayList(Arrays.asList(myAdapter.getItem(i)!!)))
            startActivity(intent)
        }
        listView.setMultiChoiceModeListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        requestPermissions(requestForMap)
    }

    private fun loadMarkers() {
        if (map != null) {
            map!!.setOnInfoWindowLongClickListener { marker ->
                if (actionMode == null) {
                    actionMode = startActionMode(this)
                    selectedCameras.clear()
                }
                selectMarker(marker, selectCamera(marker.tag as Camera))
            }
            map!!.setOnInfoWindowClickListener { marker ->
                val camera = marker.tag as Camera
                if (actionMode != null) {
                    selectMarker(marker, selectCamera(camera))
                    if (selectedCameras.isEmpty()) {
                        actionMode!!.finish()
                    }
                } else {
                    val intent = Intent(this, CameraActivity::class.java)
                    intent.putParcelableArrayListExtra("cameras", ArrayList(Arrays.asList(camera)))
                    startActivity(intent)
                }
            }
            val builder = LatLngBounds.Builder()

            //add a marker for every camera available
            for (camera in cameras) {
                val m = map!!.addMarker(MarkerOptions().position(LatLng(camera.lat, camera.lng)).title(camera.getName()))
                m.tag = camera
                markers.add(m)
                builder.include(m.position)
            }

            map!!.setLatLngBoundsForCameraTarget(builder.build())
            map!!.setOnMapLoadedCallback {
                map!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50))
            }
        }
    }

    private fun requestPermissions(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionArray, requestCode)
        } else {
            when (requestCode) {
                0 ->
                    sortByDistance(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))
                1 ->
                    map!!.isMyLocationEnabled = true
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(requestForList)
                }
            }
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(requestForMap)
                }
            }
        }
    }


    private fun sortByDistance(location: Location) {
        myAdapter.sort(SortByDistance(location))
        sectionIndex.visibility = View.INVISIBLE
        sortDistance?.isVisible = false
        sortName?.isVisible = true
    }

    private fun selectCamera(camera: Camera): Boolean {
        if (selectedCameras.contains(camera)) {
            selectedCameras.remove(camera)
        } else {
            selectedCameras.add(camera)
        }
        addFav!!.isVisible = true
        removeFav!!.isVisible = false
        if (selectedCameras.filter({ it.isFavourite }).size == selectedCameras.size) {
            addFav!!.isVisible = false
            removeFav!!.isVisible = true
        }

        showCameras!!.isVisible = selectedCameras.size < maxCameras

        if (!selectedCameras.isEmpty()) {
            actionMode?.title = String.format(resources.getQuantityString(R.plurals.selectedCameras, selectedCameras.size), selectedCameras.size)
        }
        return selectedCameras.contains(camera)
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
                startActivity(intent)
                return true
            }
            R.id.add_favourites -> {
                addRemoveFavourites(true)
                return true
            }
            R.id.remove_favourite -> {
                addRemoveFavourites(false)
                return true
            }
        }

        return false
    }

    private fun addRemoveFavourites(willAdd: Boolean) {
        val favs = sharedPrefs.getStringSet("favourites", HashSet<String>())
        val editor = sharedPrefs.edit()
        if (willAdd) {
            favs.addAll(selectedCameras.map { it.num.toString() })
        } else {
            favs.removeAll(selectedCameras.map { it.num.toString() })
        }
        for(camera in cameras){
            if(selectedCameras.contains(camera)){
                camera.isFavourite = willAdd
            }
        }
        editor.putStringSet("favourites", favs)
        editor.apply()

        addFav!!.isVisible = !willAdd
        removeFav!!.isVisible = willAdd

        myAdapter.notifyDataSetChanged()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        selectedCameras.clear()
        actionMode = mode
        actionMode!!.menuInflater.inflate(R.menu.contextual_menu, menu)
        showCameras = menu!!.findItem(R.id.open_cameras)
        removeFav = menu.findItem(R.id.remove_favourite)
        removeFav!!.isVisible = false
        addFav = menu.findItem(R.id.add_favourites)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        markers
                .filter { selectedCameras.contains(it.tag) }
                .forEach { selectMarker(it, false) }
        selectedCameras.clear()
        actionMode = null
    }

    override fun onItemCheckedStateChanged(actionMode: android.view.ActionMode, i: Int, l: Long, b: Boolean) {
        if (!selectCamera(myAdapter.getItem(i)!!) && b) {
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