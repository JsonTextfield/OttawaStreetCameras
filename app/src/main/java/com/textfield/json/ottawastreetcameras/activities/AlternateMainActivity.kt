package com.textfield.json.ottawastreetcameras.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.TextUtils
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
import com.textfield.json.ottawastreetcameras.Camera
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortByDistance
import com.textfield.json.ottawastreetcameras.SortByName
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONObject
import java.util.*

class AlternateMainActivity : AppCompatActivity(), OnMapReadyCallback, ActionMode.Callback {

    private val cameras = ArrayList<Camera>()
    private val selectedCameras = ArrayList<Camera>()
    private val markers = ArrayList<Marker>()
    private val maxCameras = 4

    private var map: GoogleMap? = null

    private lateinit var viewSwitcher: ViewSwitcher
    private var actionMode: android.view.ActionMode? = null
    private lateinit var myAdapter: CameraAdapter

    private var sortName: MenuItem? = null
    private var sortDistance: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternate_main)

        viewSwitcher = findViewById<ViewSwitcher>(R.id.view_switcher)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        downloadJson()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItemMap -> {
                viewSwitcher.showNext()
            }
            R.id.sort_name -> {
                myAdapter.sort(SortByName())
                indexHolder.visibility = View.VISIBLE
                sortName?.isVisible = false
                sortDistance?.isVisible = true
            }
            R.id.distance_sort -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            0)
                    // Permission is not granted
                } else {
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    myAdapter.sort(SortByDistance(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)))
                    indexHolder.visibility = View.INVISIBLE
                    sortDistance?.isVisible = false
                    sortName?.isVisible = true
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    myAdapter.sort(SortByDistance(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)))
                    sortDistance?.isVisible = false
                    sortName?.isVisible = true
                    indexHolder.visibility = View.INVISIBLE
                }
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_alternate, menu)

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
                if (TextUtils.isEmpty(newText)) {
                    myAdapter.filter.filter("")
                    listView.clearTextFilter()
                    indexHolder.visibility = View.VISIBLE
                } else {
                    myAdapter.filter.filter(newText)
                    indexHolder.visibility = View.INVISIBLE
                }
                return true
            }
        })

        return true
    }

    private fun downloadJson() {

        val url = "https://traffic.ottawa.ca/map/camera_list"
        val queue = Volley.newRequestQueue(this)
        val jsObjRequest = JsonArrayRequest(url, Response.Listener { response ->
            (0 until response.length())
                    .map { response.get(it) as JSONObject }
                    .forEach { cameras.add(Camera(it)) }

            Collections.sort(cameras, SortByName())
            myAdapter = CameraAdapter(this, cameras)
            listView.adapter = myAdapter

            toolbar.setOnClickListener { listView.setSelection(0) }
            setSupportActionBar(toolbar)

            setupSectionIndex()
            setupListView()
            loadMarkers()

        }, Response.ErrorListener {
            val builder = AlertDialog.Builder(this)

            builder.setTitle(resources.getString(R.string.no_network_title)).setMessage(resources.getString(R.string.no_network_content))
                    .setPositiveButton("OK") { _, _ -> finish() }
            val dialog = builder.create()
            dialog.show()
        })
        queue.add(jsObjRequest)
    }

    private fun setupSectionIndex() {
        val index = HashSet<Char>()

        //assumes cameras are sorted
        for (i in 0 until cameras.size) {

            //get the first character
            val c = cameras[i].getName().replace(Regex("\\W"), "")[0]

            if (!index.contains(c)) {
                index.add(c)
                val t = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL

                t.layoutParams = layoutParams
                t.text = c.toString()
                t.textSize = 10f
                t.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                t.gravity = Gravity.CENTER_HORIZONTAL
                t.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
                t.setOnClickListener { listView.setSelection(i) }

                indexHolder.addView(t)
            }

        }
    }

    private fun setupListView() {
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->

            val intent = Intent(this, CameraActivity::class.java)
            intent.putParcelableArrayListExtra("cameras", ArrayList(Arrays.asList(myAdapter.getItem(i)!!)))
            startActivity(intent)
        }
        //listView.itemsCanFocus = true

        listView.setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {
            override fun onItemCheckedStateChanged(actionMode: android.view.ActionMode, i: Int, l: Long, b: Boolean) {
                val camera = myAdapter.getItem(i)!!
                if (b) {
                    if (selectedCameras.size < maxCameras) {
                        selectedCameras.add(camera)
                    } else {
                        selectedCameras.remove(camera)
                        listView.setItemChecked(i, false)
                    }
                } else {
                    selectedCameras.remove(camera)
                }
            }

            override fun onCreateActionMode(actionMode: android.view.ActionMode, menu: Menu): Boolean {
                actionMode.menuInflater.inflate(R.menu.contextual_menu, menu)
                return true
            }

            override fun onPrepareActionMode(actionMode: android.view.ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(actionMode: android.view.ActionMode, menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.open_cameras) {
                    val intent = Intent(this@AlternateMainActivity, CameraActivity::class.java)
                    intent.putParcelableArrayListExtra("cameras", selectedCameras)
                    startActivity(intent)
                    return true
                }
                return false
            }

            override fun onDestroyActionMode(actionMode: android.view.ActionMode) {
                selectedCameras.clear()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    private fun loadMarkers() {
        if (map != null) {
            map!!.setOnInfoWindowLongClickListener { marker ->
                actionMode = startActionMode(this)
                selectedCameras.add(marker.tag as Camera)
                selectMarker(marker, true)
            }

            map!!.setOnInfoWindowClickListener { marker ->
                val camera = marker.tag as Camera
                if (actionMode != null) {
                    if (!selectedCameras.contains(camera) && selectedCameras.size < maxCameras) {
                        selectedCameras.add(camera)
                        selectMarker(marker, true)
                    } else {
                        selectedCameras.remove(camera)
                        selectMarker(marker, false)

                        if (selectedCameras.isEmpty()) {
                            actionMode?.finish()
                        }
                    }
                } else {
                    selectedCameras.add(marker.tag as Camera)
                    val intent = Intent(this, CameraActivity::class.java)
                    intent.putParcelableArrayListExtra("cameras", selectedCameras)
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

            map!!.setOnMapLoadedCallback {
                map!!.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50))
            }
        }
    }

    private fun selectMarker(marker: Marker, boolean: Boolean) {
        if (boolean) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        } else {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker())
        }
    }


    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.open_cameras) {
            val intent = Intent(this, CameraActivity::class.java)
            intent.putParcelableArrayListExtra("cameras", selectedCameras)
            startActivity(intent)
            return true
        }
        return false
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode!!.menuInflater.inflate(R.menu.contextual_menu, menu)
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
    }
}
