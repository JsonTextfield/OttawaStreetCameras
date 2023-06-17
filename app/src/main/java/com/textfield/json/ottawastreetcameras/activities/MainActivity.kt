package com.textfield.json.ottawastreetcameras.activities

import android.Manifest.permission
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.widget.AdapterView
import android.widget.Filter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
import com.textfield.json.ottawastreetcameras.adapters.filters.CameraFilter
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.databinding.ActivityMainBinding
import com.textfield.json.ottawastreetcameras.entities.Camera
import java.util.Collections


class MainActivity : GenericActivity(), OnMapReadyCallback {
    private var showingMap = false

    private val requestForList = 0
    private val requestForMap = 1
    private val maxCameras = 8

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraManager: CameraManager

    private var map: GoogleMap? = null
    private var mapIsLoaded = false
    private var searchView: SearchView? = null
    private var neighbourhoodSearchView: SearchView? = null
    private var searchMenuItem: MenuItem? = null
    private var neighbourhoodSearchMenuItem: MenuItem? = null

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
        galleryView = binding.galleryView
        val width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
        val scale = resources.displayMetrics.density
        val pixels = (100 * scale + 0.5f).toInt()
        galleryView.numColumns = kotlin.math.max(3, (width / pixels).coerceIn(3, 9))
        adapter = object : CameraAdapter(this, cameras) {
            override fun onComplete() {
                binding.sectionIndexListview.updateIndex()
            }
        }
        //galleryAdapter = GalleryAdapter (this, cameras)
        listView.adapter = adapter
        //galleryView.adapter = galleryAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            selectCamera(adapter.getItem(i)!!)
            showSelectedCameras()
        }
        listView.setMultiChoiceModeListener(this)
        binding.toolbar.setOnClickListener { listView.setSelection(0) }
        setSupportActionBar(binding.toolbar)

        cameraManager = CameraManager.getInstance(this@MainActivity)

        cameraManager.downloadAll {
            cameras = cameraManager.allCameras
            loadList()
        }

    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (featureId == Window.FEATURE_ACTION_BAR) {
            if (menu.javaClass.simpleName == "MenuBuilder") {
                try {
                    val m = menu.javaClass.getDeclaredMethod(
                        "setOptionalIconsVisible", java.lang.Boolean.TYPE
                    )
                    m.isAccessible = true
                    m.invoke(menu, true)
                } catch (e: NoSuchMethodException) {
                    Log.e("", "onMenuOpened", e)
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }
        return super.onMenuOpened(featureId, menu!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        searchMenuItem = menu.findItem(R.id.camera_searchView)
        neighbourhoodSearchMenuItem = menu.findItem(R.id.neighbourhood_searchView)
        val nightMode = menu.findItem(R.id.night_mode)
        nightMode.isChecked = isNightModeOn()
        searchView = searchMenuItem?.actionView as SearchView?
        neighbourhoodSearchView = neighbourhoodSearchMenuItem?.actionView as SearchView?
        neighbourhoodSearchView


        searchView?.queryHint = resources.getQuantityString(R.plurals.search_hint, cameras.size, cameras.size)
        neighbourhoodSearchView?.queryHint =
            resources.getQuantityString(R.plurals.search_hint_neighbourhood, neighbourhoods.size, neighbourhoods.size)

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
                    if (newText.isNotEmpty())
                        View.INVISIBLE
                    else
                        View.VISIBLE
                return true
            }
        })
        (0 until menu.size()).forEach { tintMenuItemIcon(menu.getItem(it)) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItemMap -> {
                showingMap = !showingMap
                item.setIcon(if (showingMap) R.drawable.baseline_list_24 else R.drawable.baseline_place_24)
                binding.viewSwitcher.showNext()
                invalidateOptionsMenu()
            }

            R.id.sort_name -> {
                adapter.sort(SortByName())

                binding.sectionIndexListview.sectionIndex.visibility = View.VISIBLE
            }

            R.id.sort_distance -> {
                requestPermissions(requestForList)
            }

            R.id.sort_neighbourhood -> {
                adapter.sort(SortByNeighbourhood())

                binding.sectionIndexListview.sectionIndex.visibility = View.INVISIBLE
            }

            R.id.random_camera -> {
                selectCamera(cameras.random())
                showSelectedCameras()
            }

            R.id.shuffle -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putParcelableArrayListExtra("cameras", cameras)
                intent.putExtra("shuffle", true)
                getResult.launch(intent)
            }

            R.id.favourites -> {
                supportActionBar?.title = if (supportActionBar?.title == "Favourites") "StreetCams" else "Favourites"
                adapter.clear()
                adapter.addAll(cameraManager.filterDisplayedCameras(FilterMode.FAVOURITE))
                adapter.notifyDataSetChanged()
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

            R.id.about -> {
                AlertDialog.Builder(this)
                    .setTitle(resources.getString(R.string.app_name_long))
                    .setMessage(
                        resources.getString(
                            R.string.version,
                            com.textfield.json.ottawastreetcameras.BuildConfig.VERSION_NAME
                        )
                    )
                    .setNegativeButton(R.string.rate) { _, _ -> rateApp() }
                    .setPositiveButton(R.string.licences) { _, _ ->
                        startActivity(
                            Intent(
                                this,
                                OssLicensesMenuActivity::class.java
                            )
                        )
                    }
                    .show()
            }
        }
        return true
    }

    private fun rateApp() {
        val manager = ReviewManagerFactory.create(this@MainActivity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(this@MainActivity, reviewInfo)
            } else {
                Log.w("rateApp", task.exception)
            }
        }
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        super.onCreateActionMode(mode, menu)
        saveImage.isVisible = false
        return true
    }

    private fun loadList() {
        Collections.sort(cameras, SortByName())
        adapter.clear()
        adapter.addAll(cameras)
        //galleryAdapter.addAll(cameras)
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
                        .position(LatLng(camera.lat, camera.lon))
                        .title(camera.getName())
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(
                                if (camera.isFavourite) BitmapDescriptorFactory.HUE_YELLOW
                                else BitmapDescriptorFactory.HUE_RED
                            )
                        )
                )
                m?.let { marker ->
                    marker.tag = camera
                    camera.marker = marker
                    builder.include(marker.position)
                    marker.isVisible = camera.isVisible
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
        val noPermissionsGranted = permissionArray.all {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (noPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissionArray, requestCode)
        } else {
            when (requestCode) {
                requestForList -> {
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && lastLocation != null) {
                        adapter.sort(SortByDistance(lastLocation))
                        binding.sectionIndexListview.sectionIndex.visibility = View.INVISIBLE
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
        if (PackageManager.PERMISSION_GRANTED in grantResults) {
            requestPermissions(requestCode)
        }
    }

    override fun selectCamera(camera: Camera): Boolean {
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