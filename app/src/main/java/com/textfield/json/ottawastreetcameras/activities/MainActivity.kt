package com.textfield.json.ottawastreetcameras.activities

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Filter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsRequestQueue
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
import com.textfield.json.ottawastreetcameras.adapters.filters.CameraFilter
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.databinding.ActivityMainBinding
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class MainActivity : GenericActivity(), OnMapReadyCallback {
    private val requestForList = 0
    private val requestForMap = 1
    private val maxCameras = 8

    private lateinit var binding: ActivityMainBinding

    private var map: GoogleMap? = null

    private var mapIsLoaded = false
    private var searchView: SearchView? = null
    private var searchMenuItem: MenuItem? = null
    private var sortName: MenuItem? = null
    private var sortDistance: MenuItem? = null
    private var sortNeighbourhood: MenuItem? = null

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (actionMode == null) {
            selectedCameras.clear()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.sectionIndexListview.defaultTextColour = if (isNightModeOn()) Color.WHITE else Color.BLACK
        binding.progressBar.visibility = View.VISIBLE

        listView = binding.sectionIndexListview.listView
        adapter = object : CameraAdapter(this, cameras) {
            override fun onComplete() {
                binding.sectionIndexListview.updateIndex()
            }
        }
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            selectCamera(adapter.getItem(i)!!)
            showSelectedCameras()
        }
        listView.setMultiChoiceModeListener(this)
        binding.toolbar.setOnClickListener {
            listView.setSelection(0)
        }
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            downloadCameraList()
        } else {
            cameras = savedInstanceState.getParcelableArrayList("cameras") ?: cameras
            loadList()
            if (savedInstanceState.getParcelableArrayList<Camera>("selectedCameras") != null) {
                previouslySelectedCameras = savedInstanceState.getParcelableArrayList("selectedCameras")!!
                startActionMode(this@MainActivity)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        sortName = menu.findItem(R.id.sort_name)
        sortDistance = menu.findItem(R.id.sort_distance)
        sortNeighbourhood = menu.findItem(R.id.sort_neighbourhood)
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

                adapter.filter.filter(newText)
                binding.sectionIndexListview.sectionIndex.visibility =
                    if (newText.isNotEmpty() || sortName?.isVisible!!)
                        View.INVISIBLE
                    else
                        View.VISIBLE
                return true
            }
        })
        (0 until menu.size()).forEach {
            tintMenuItemIcon(menu.getItem(it))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItemMap -> {
                binding.viewSwitcher.showNext()
            }
            R.id.sort_name -> {
                adapter.sort(SortByName())

                binding.sectionIndexListview.sectionIndex.visibility = View.VISIBLE
                sortDistance?.isVisible = true
                sortNeighbourhood?.isVisible = true
                sortName?.isVisible = false
            }
            R.id.sort_distance -> {
                requestPermissions(requestForList)
            }
            R.id.sort_neighbourhood -> {
                adapter.sort(SortByNeighbourhood())

                sortNeighbourhood?.isVisible = false
                sortDistance?.isVisible = true
                sortName?.isVisible = true
            }
            R.id.random_camera -> {
                selectCamera(cameras[Random().nextInt(cameras.size)])
                showSelectedCameras()
            }
            R.id.shuffle -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putParcelableArrayListExtra("cameras", cameras)
                intent.putExtra("shuffle", true)
                getResult.launch(intent)
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
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
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

    private fun downloadNeighbourhoodList() {
        val jsonObjectRequest = JsonObjectRequest(
            "https://services.arcgis.com/G6F8XLCl5KtAlZ2G/arcgis/rest/services/Gen_2_ONS_Boundaries/FeatureServer/0/query?outFields=*&where=1%3D1&f=geojson",
            { response ->
                val jsonArray = response.getJSONArray("features")
                neighbourhoods = (0 until jsonArray.length()).map {
                    val neighbourhood = Neighbourhood(jsonArray[it] as JSONObject)
                    for (camera in cameras) {
                        if (neighbourhood.containsCamera(camera)) {
                            camera.neighbourhood = neighbourhood.getName()
                        }
                    }
                    neighbourhood
                } as ArrayList<Neighbourhood>

                loadList()
            }, {
                it.printStackTrace()
            })
        CoroutineScope(Dispatchers.IO).launch {
            Volley.newRequestQueue(this@MainActivity).add(jsonObjectRequest)
        }

    }

    private fun downloadCameraList() {
        val jsObjRequest = JsonArrayRequest("https://traffic.ottawa.ca/beta/camera_list", { response ->
            val sharedPrefs = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
            cameras = (0 until response.length()).map {
                Camera(response.getJSONObject(it)).apply {
                    setFavourite(num.toString() in sharedPrefs.getStringSet(prefNameFavourites, HashSet())!!)
                    setVisible(num.toString() !in sharedPrefs.getStringSet(prefNameHidden, HashSet())!!)
                }
            } as ArrayList<Camera>
            downloadNeighbourhoodList()
        }, {
            it.printStackTrace()
            showErrorDialogue(this)
        })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(this@MainActivity).add(jsObjRequest)
        }
    }

    private fun loadList() {
        Collections.sort(cameras, SortByName())
        adapter.clear()
        adapter.addAll(cameras)
        searchView?.queryHint = resources.getQuantityString(R.plurals.search_hint, cameras.size, cameras.size)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        binding.sectionIndexListview.updateIndex()
        binding.progressBar.visibility = View.INVISIBLE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (isNightModeOn()) {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MainActivity, R.raw.dark_mode))
        }
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
                val m = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(camera.lat, camera.lng))
                        .title(camera.getName())
                )
                m?.let {
                    if (camera.isFavourite) {
                        it.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    } else {
                        it.setIcon(BitmapDescriptorFactory.defaultMarker())
                    }
                    it.tag = camera
                    camera.marker = m
                    builder.include(m.position)

                    it.isVisible = camera.isVisible
                }
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


    private fun requestPermissions(requestCode: Int) {
        val permissionArray = arrayOf(
            permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_COARSE_LOCATION
        )
        val allTrue = permissionArray.all {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (allTrue) {
            ActivityCompat.requestPermissions(this, permissionArray, requestCode)
        } else {
            when (requestCode) {
                requestForList -> {
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && lastLocation != null) {
                        adapter.sort(SortByDistance(lastLocation))
                        binding.sectionIndexListview.sectionIndex.visibility = View.INVISIBLE
                        sortDistance?.isVisible = false
                        sortName?.isVisible = true
                        sortNeighbourhood?.isVisible = true
                    } else {
                        Snackbar.make(listView, getString(R.string.location_unavailable), Snackbar.LENGTH_LONG).show()
                    }
                }
                requestForMap -> map?.isMyLocationEnabled = true
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        if (actionMode != null) {
            showCameras.isVisible = selectedCameras.size <= maxCameras
        }
        return result
    }

    private fun showSelectedCameras() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putParcelableArrayListExtra("cameras", selectedCameras)
        getResult.launch(intent)
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
            adapter.filter.filter((searchMenuItem!!.actionView as SearchView).query)
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
                    latLngBounds.include(camera.marker?.position!!)
                    anyVisible = true
                }
            }
            if (anyVisible && mapIsLoaded) {
                animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 50))
            }
        }
    }
}