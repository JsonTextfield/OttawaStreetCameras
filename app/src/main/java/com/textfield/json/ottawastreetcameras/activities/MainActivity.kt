@file:OptIn(ExperimentalMaterial3Api::class)

package com.textfield.json.ottawastreetcameras.activities

import android.Manifest.permission
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Filter
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.multidex.BuildConfig
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
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.SortMode
import com.textfield.json.ottawastreetcameras.ViewMode
import com.textfield.json.ottawastreetcameras.adapters.CameraAdapter
import com.textfield.json.ottawastreetcameras.adapters.filters.CameraFilter
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.databinding.ActivityMainBinding
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.AppTheme
import com.textfield.json.ottawastreetcameras.ui.CameraGalleryView
import com.textfield.json.ottawastreetcameras.ui.CameraListView
import com.textfield.json.ottawastreetcameras.ui.MenuItem
import com.textfield.json.ottawastreetcameras.ui.NeighbourhoodSearchBar
import com.textfield.json.ottawastreetcameras.ui.OverflowMenuItem
import com.textfield.json.ottawastreetcameras.ui.SearchBar
import com.textfield.json.ottawastreetcameras.ui.SectionIndex
import com.textfield.json.ottawastreetcameras.ui.StreetCamsMap
import com.textfield.json.ottawastreetcameras.ui.Visibility
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

    @Composable
    fun ToolbarActions() {
        val width = LocalConfiguration.current.screenWidthDp
        var remainingActions = width / 48 / 2
        Log.e("WIDTH", width.toString())
        Log.e("MAX_ACTIONS", remainingActions.toString())

        Box() {
            var showViewModeMenu by remember { mutableStateOf(false) }
            ViewModeMenu(showViewModeMenu) {
                showViewModeMenu = false
            }
            MenuItem(
                icon = when (cameraManager.viewMode) {
                    ViewMode.LIST -> {
                        Icons.Rounded.List
                    }

                    ViewMode.MAP -> {
                        Icons.Filled.Place
                    }

                    ViewMode.GALLERY -> {
                        Icons.Rounded.GridView
                    }
                },
                tooltip = when (cameraManager.viewMode) {
                    ViewMode.LIST -> {
                        getString(R.string.list)
                    }

                    ViewMode.MAP -> {
                        getString(R.string.map)
                    }

                    ViewMode.GALLERY -> {
                        getString(R.string.gallery)
                    }
                },
                visible = remainingActions-- > 0
            ) {
                showViewModeMenu = !showViewModeMenu
            }
        }

        Box() {
            var showSortMenu by remember { mutableStateOf(false) }
            SortMenu(showSortMenu) {
                showSortMenu = false
            }

            MenuItem(icon = Icons.Rounded.Sort, tooltip = getString(R.string.sort), visible = remainingActions-- > 0) {
                showSortMenu = !showSortMenu
            }
        }
        MenuItem(icon = Icons.Rounded.Search, tooltip = getString(R.string.search), visible = remainingActions-- > 0) {

            cameraManager.searchMode =
                if (cameraManager.searchMode != SearchMode.NAME) SearchMode.NAME else SearchMode.NONE
            loadView()
        }
        MenuItem(
            icon = Icons.Rounded.TravelExplore,
            tooltip = getString(R.string.search_neighbourhood),
            visible = remainingActions-- > 0
        ) {
            cameraManager.searchMode =
                if (cameraManager.searchMode != SearchMode.NEIGHBOURHOOD) SearchMode.NEIGHBOURHOOD else SearchMode.NONE
            loadView()
        }
        MenuItem(
            icon = Icons.Rounded.Star,
            tooltip = getString(R.string.favourites),
            visible = remainingActions-- > 0
        ) {
            cameraManager.filterMode =
                if (cameraManager.filterMode == FilterMode.FAVOURITE) FilterMode.VISIBLE else FilterMode.FAVOURITE
            loadView()
        }
        MenuItem(
            icon = Icons.Rounded.VisibilityOff,
            tooltip = getString(R.string.hide),
            visible = remainingActions-- > 0
        ) {
            cameraManager.filterMode =
                if (cameraManager.filterMode == FilterMode.HIDDEN) FilterMode.VISIBLE else FilterMode.HIDDEN
            loadView()
        }
        MenuItem(
            icon = Icons.Rounded.Casino,
            tooltip = getString(R.string.random_camera),
            visible = remainingActions-- > 0
        ) {
            selectCamera(cameras.random())
            showSelectedCameras()
        }
        MenuItem(
            icon = Icons.Rounded.Shuffle,
            tooltip = getString(R.string.shuffle),
            visible = remainingActions-- > 0
        ) {
            showCameras(true)
        }
        MenuItem(icon = Icons.Rounded.Info, tooltip = getString(R.string.about), visible = remainingActions-- > 0) {
            showAboutDialog()
        }
        if (remainingActions < 0) {
            Box() {
                var showOverflowMenu by remember { mutableStateOf(false) }
                OverflowMenu(showOverflowMenu) { showOverflowMenu = false }
                MenuItem(icon = Icons.Rounded.MoreVert, tooltip = "More", visible = true) {
                    showOverflowMenu = true
                }
            }
        }

    }

    @Composable
    fun SortMenu(expanded: Boolean, onItemSelected: () -> Unit) {
        DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected() }) {
            fun setSortMode(sortMode: SortMode) {
                cameraManager.sortMode = sortMode
                loadView()
                onItemSelected()
            }

            DropdownMenuItem(
                text = { Text(getString(R.string.sort_by_name)) },
                leadingIcon = {
                    RadioButton(
                        selected = cameraManager.sortMode == SortMode.NAME,
                        onClick = {
                            setSortMode(SortMode.NAME)
                        },
                    )
                },
                onClick = {
                    setSortMode(SortMode.NAME)
                },
            )
            DropdownMenuItem(
                text = { Text(getString(R.string.sort_by_distance)) },
                leadingIcon = {
                    RadioButton(
                        selected = cameraManager.sortMode == SortMode.DISTANCE,
                        onClick = { setSortMode(SortMode.DISTANCE) },
                    )
                },
                onClick = {
                    setSortMode(SortMode.DISTANCE)
                },
            )
            DropdownMenuItem(
                text = { Text(getString(R.string.sort_by_neighbourhood)) },
                leadingIcon = {
                    RadioButton(
                        selected = cameraManager.sortMode == SortMode.NEIGHBOURHOOD,
                        onClick = { setSortMode(SortMode.NEIGHBOURHOOD) },
                    )
                },
                onClick = {
                    setSortMode(SortMode.NEIGHBOURHOOD)
                },
            )
        }
    }

    @Composable
    fun OverflowMenu(expanded: Boolean, onItemSelected: () -> Unit) {
        val width = LocalConfiguration.current.screenWidthDp
        var remainingActions = width / 48 / 2 - 1

        Log.e("WIDTH", width.toString())
        Log.e("REMAINING_ACTIONS", remainingActions.toString())

        DropdownMenu(expanded = expanded, onDismissRequest = onItemSelected) {

            Box() {
                var showViewModeMenu by remember { mutableStateOf(false) }
                ViewModeMenu(showViewModeMenu) {
                    showViewModeMenu = false
                }
                OverflowMenuItem(
                    icon = when (cameraManager.viewMode) {
                        ViewMode.LIST -> {
                            Icons.Rounded.List
                        }

                        ViewMode.MAP -> {
                            Icons.Filled.Place
                        }

                        ViewMode.GALLERY -> {
                            Icons.Rounded.GridView
                        }
                    },
                    tooltip = getString(R.string.list),
                    visible = cameraManager.viewMode != ViewMode.LIST && remainingActions-- < 1
                ) {
                    showViewModeMenu = !showViewModeMenu
                    onItemSelected()
                }
                var showSortMenu by remember { mutableStateOf(false) }
                SortMenu(showSortMenu) {
                    showSortMenu = false
                }

                OverflowMenuItem(
                    icon = Icons.Rounded.Sort,
                    tooltip = getString(R.string.sort),
                    visible = remainingActions-- < 1
                ) {
                    showSortMenu = !showSortMenu
                    onItemSelected()
                }
            }
            OverflowMenuItem(
                icon = Icons.Rounded.Search,
                tooltip = getString(R.string.search),
                visible = remainingActions-- < 1
            ) {
                cameraManager.searchMode =
                    if (cameraManager.searchMode != SearchMode.NAME) SearchMode.NAME else SearchMode.NONE
                loadView()
                onItemSelected()
            }
            OverflowMenuItem(
                icon = Icons.Rounded.TravelExplore,
                tooltip = getString(R.string.search_neighbourhood),
                visible = remainingActions-- < 1
            ) {
                cameraManager.searchMode =
                    if (cameraManager.searchMode != SearchMode.NEIGHBOURHOOD) SearchMode.NEIGHBOURHOOD else SearchMode.NONE
                loadView()
                onItemSelected()
            }
            OverflowMenuItem(
                icon = Icons.Rounded.Star,
                tooltip = getString(R.string.favourites),
                visible = remainingActions-- < 1
            ) {
                cameraManager.filterMode =
                    if (cameraManager.filterMode == FilterMode.FAVOURITE) FilterMode.VISIBLE else FilterMode.FAVOURITE
                loadView()
                onItemSelected()
            }
            OverflowMenuItem(
                icon = Icons.Rounded.VisibilityOff,
                tooltip = getString(R.string.hidden_cameras),
                visible = remainingActions-- < 1
            ) {
                cameraManager.filterMode =
                    if (cameraManager.filterMode == FilterMode.HIDDEN) FilterMode.VISIBLE else FilterMode.HIDDEN
                loadView()
                onItemSelected()
            }
            OverflowMenuItem(
                icon = Icons.Rounded.Casino,
                tooltip = getString(R.string.random_camera),
                visible = remainingActions-- < 1
            ) {
                selectCamera(cameras.random())
                showSelectedCameras()
                onItemSelected()
            }
            OverflowMenuItem(
                icon = Icons.Rounded.Shuffle,
                tooltip = getString(R.string.shuffle),
                visible = remainingActions-- < 1
            ) {
                showCameras(true)
                onItemSelected()
            }
            OverflowMenuItem(
                icon = Icons.Rounded.Info,
                tooltip = getString(R.string.about),
                visible = remainingActions-- < 1
            ) {
                showAboutDialog()
                onItemSelected()
            }
        }
    }

    @Composable
    fun ViewModeMenu(expanded: Boolean, onItemSelected: () -> Unit) {
        DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected() }) {
            fun setViewMode(viewMode: ViewMode) {
                cameraManager.viewMode = viewMode
                loadView()
                onItemSelected()
            }
            DropdownMenuItem(
                text = { Text(getString(R.string.list)) },
                leadingIcon = {
                    RadioButton(
                        selected = cameraManager.viewMode == ViewMode.LIST,
                        onClick = {
                            setViewMode(ViewMode.LIST)
                        },
                    )
                },
                onClick = {
                    setViewMode(ViewMode.LIST)
                },
            )
            DropdownMenuItem(
                text = { Text(getString(R.string.map)) },
                leadingIcon = {
                    RadioButton(
                        selected = cameraManager.viewMode == ViewMode.MAP,
                        onClick = {
                            setViewMode(ViewMode.MAP)
                        },
                    )
                },
                onClick = {
                    setViewMode(ViewMode.MAP)
                },
            )
            DropdownMenuItem(
                text = { Text(getString(R.string.gallery)) },
                leadingIcon = {
                    RadioButton(
                        selected = cameraManager.viewMode == ViewMode.GALLERY,
                        onClick = {
                            setViewMode(ViewMode.GALLERY)
                        },
                    )
                },
                onClick = {
                    setViewMode(ViewMode.GALLERY)
                },
            )
        }
    }

    @Composable
    fun ActionModeMenu() {

        MenuItem(icon = Icons.Rounded.Clear, tooltip = "Clear", visible = false) {

        }

        MenuItem(icon = Icons.Rounded.SelectAll, tooltip = getString(R.string.select_all), visible = false) {

        }
        MenuItem(icon = Icons.Rounded.CameraAlt, tooltip = getString(R.string.show), visible = false) {

        }
        MenuItem(icon = Icons.Rounded.VisibilityOff, tooltip = getString(R.string.hide), visible = false) {

        }
        MenuItem(icon = Icons.Rounded.Visibility, tooltip = getString(R.string.unhide), visible = false) {

        }
        MenuItem(icon = Icons.Rounded.Star, tooltip = getString(R.string.add_to_favourites), visible = false) {

        }
        MenuItem(
            icon = Icons.Rounded.StarBorder,
            tooltip = getString(R.string.remove_from_favourites),
            visible = false
        ) {

        }
    }

    private fun showCameras(shuffle: Boolean = false) {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putParcelableArrayListExtra("cameras", cameras)
        intent.putExtra("shuffle", shuffle)
        getResult.launch(intent)
    }

    @Composable
    fun MainAppBar() {
        TopAppBar(
            title = {
                when (cameraManager.searchMode) {
                    SearchMode.NONE -> {
                        Text(resources.getString(R.string.app_name))
                    }

                    SearchMode.NAME -> {
                        SearchBar(
                            resources.getQuantityString(
                                R.plurals.search_hint,
                                cameras.size,
                                cameras.size
                            )
                        ) {
                            cameras = cameraManager.searchDisplayedCameras(SearchMode.NAME, it)
                            loadView()
                        }
                    }

                    SearchMode.NEIGHBOURHOOD -> {
                        NeighbourhoodSearchBar(
                            resources.getQuantityString(
                                R.plurals.search_hint_neighbourhood,
                                neighbourhoods.size,
                                neighbourhoods.size
                            )
                        ) {
                            cameras = cameraManager.searchDisplayedCameras(SearchMode.NEIGHBOURHOOD, it)
                            loadView()
                        }
                    }
                }
            },
            actions = {
                ToolbarActions()
            }
        )
    }

    @Composable
    fun MainContent(padding: PaddingValues) {
        Column(modifier = Modifier.padding(padding)) {
            val displayedCameras = when (cameraManager.filterMode) {
                FilterMode.HIDDEN -> {
                    cameras.filter {
                        !it.isVisible
                    }
                }

                FilterMode.FAVOURITE -> {
                    cameras.filter {
                        it.isFavourite
                    }
                }

                FilterMode.VISIBLE -> {
                    cameras.filter {
                        it.isVisible
                    }
                }
            }
            when (cameraManager.viewMode) {
                ViewMode.LIST -> {
                    Row {
                        Visibility(visible = cameraManager.sortMode == SortMode.NAME && cameraManager.searchMode == SearchMode.NONE) {
                            SectionIndex(displayedCameras)
                        }
                        CameraListView(
                            displayedCameras,
                            onItemClick = { showCamera(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                ViewMode.MAP -> {
                    StreetCamsMap(displayedCameras) { showCamera(it) }
                }

                ViewMode.GALLERY -> {
                    CameraGalleryView(displayedCameras) { showCamera(it) }
                }
            }
        }
    }

    private fun loadView() {
        when (cameraManager.sortMode) {
            SortMode.NAME -> {
                cameras.sortWith(SortByName())
            }

            SortMode.NEIGHBOURHOOD -> {
                cameras.sortWith(SortByNeighbourhood())
            }

            SortMode.DISTANCE -> {
                requestPermissions(requestForList)
            }
        }
        setContent {
            AppTheme {
                Scaffold(
                    topBar = {
                        MainAppBar()
                    },
                    content = {
                        MainContent(it)
                    },
                )
            }
        }
    }

    private fun showCamera(camera: Camera) {
        selectedCameras.clear()
        selectedCameras.add(camera)
        val intent = Intent(this@MainActivity, CameraActivity::class.java)
        intent.putParcelableArrayListExtra("cameras", selectedCameras)
        getResult.launch(intent)
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.app_name_long))
            .setMessage(
                resources.getString(
                    R.string.version,
                    BuildConfig.VERSION_NAME
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContent {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(color = Color(0xFF11AAFF))
            }
        }*/
        cameraManager = CameraManager.getInstance()
        cameraManager.downloadAll(this) {
            cameras = cameraManager.allCameras
            neighbourhoods = cameraManager.neighbourhoods
            loadView()
        }
        loadView()
        /*binding = ActivityMainBinding.inflate(layoutInflater)
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
        */
        adapter = object : CameraAdapter(this, cameras) {
            override fun onComplete() {
                binding.sectionIndexListview.updateIndex()
            }
        }
        /*
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
    */
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
                showAboutDialog()
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
                        cameras.sortWith(SortByDistance(lastLocation))
                        //binding.sectionIndexListview.sectionIndex.visibility = View.INVISIBLE
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