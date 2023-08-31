@file:OptIn(ExperimentalMaterial3Api::class)

package com.textfield.json.ottawastreetcameras.ui.activities

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.UIState
import com.textfield.json.ottawastreetcameras.ViewMode
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.components.AboutDialog
import com.textfield.json.ottawastreetcameras.ui.components.AppBarTitle
import com.textfield.json.ottawastreetcameras.ui.components.AppTheme
import com.textfield.json.ottawastreetcameras.ui.components.CameraGalleryView
import com.textfield.json.ottawastreetcameras.ui.components.CameraListView
import com.textfield.json.ottawastreetcameras.ui.components.CameraMapView
import com.textfield.json.ottawastreetcameras.ui.components.ErrorScreen
import com.textfield.json.ottawastreetcameras.ui.components.LoadingScreen
import com.textfield.json.ottawastreetcameras.ui.components.SectionIndex
import com.textfield.json.ottawastreetcameras.ui.components.menu.Action
import com.textfield.json.ottawastreetcameras.ui.components.menu.ActionBar
import com.textfield.json.ottawastreetcameras.ui.components.menu.SortModeMenu
import com.textfield.json.ottawastreetcameras.ui.components.menu.ViewModeMenu

class MainActivity : AppCompatActivity() {
    private val requestForList = 0
    private val requestForMap = 1

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainAppBar(cameraViewModel: CameraViewModel, listState: LazyListState) {
        val cameraState by cameraViewModel.cameraState.collectAsState()
        TopAppBar(
            navigationIcon = {
                if (cameraState.selectedCameras.isEmpty()) {
                    if (cameraState.searchMode != SearchMode.NONE) {
                        IconButton(onClick = {
                            cameraViewModel.changeSearchMode(SearchMode.NONE)
                            cameraViewModel.searchCameras("")
                        }) {
                            Icon(Icons.Rounded.ArrowBack, stringResource(id = R.string.back), tint = Color.White)
                        }
                    }
                    else if (cameraState.filterMode != FilterMode.VISIBLE) {
                        IconButton(onClick = { cameraViewModel.changeFilterMode(FilterMode.VISIBLE) }) {
                            Icon(Icons.Rounded.ArrowBack, stringResource(id = R.string.back), tint = Color.White)
                        }
                    }
                }
            },
            title = {
                AppBarTitle(cameraViewModel, listState)
            },
            actions = {
                ActionBar(getActions(cameraViewModel))
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (!isSystemInDarkTheme() || cameraState.selectedCameras.isNotEmpty()) {
                    colorResource(id = R.color.colorAccent)
                }
                else {
                    Color.Black
                }
            ),
        )
    }

    @Composable
    fun MainContent(cameraViewModel: CameraViewModel, listState: LazyListState) {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Column {
                val cameraState by cameraViewModel.cameraState.collectAsState()
                val displayedCameras = cameraState.displayedCameras
                val onItemClick = { camera: Camera ->
                    if (cameraState.selectedCameras.isNotEmpty()) {
                        cameraViewModel.selectCamera(camera)
                    }
                    else {
                        showCameras(arrayListOf(camera))
                    }
                }
                val onItemLongClick = { camera: Camera -> cameraViewModel.selectCamera(camera) }
                when (cameraState.viewMode) {
                    ViewMode.LIST -> {
                        Row {
                            AnimatedVisibility(visible = cameraState.showSectionIndex) {
                                SectionIndex(displayedCameras.map { it.sortableName }, listState)
                            }
                            CameraListView(
                                cameraViewModel,
                                displayedCameras,
                                modifier = Modifier.weight(1f),
                                listState = listState,
                                onItemClick = onItemClick,
                                onItemLongClick = onItemLongClick
                            )
                        }
                    }

                    ViewMode.MAP -> {
                        CameraMapView(
                            cameraViewModel,
                            displayedCameras,
                            isMyLocationEnabled = requestLocationPermissions(requestForMap),
                            onItemClick = onItemClick,
                            onItemLongClick = onItemLongClick
                        )
                    }

                    ViewMode.GALLERY -> {
                        CameraGalleryView(
                            cameraViewModel,
                            displayedCameras,
                            onItemClick = onItemClick,
                            onItemLongClick = onItemLongClick
                        )
                    }
                }
            }
        }
    }

    private fun loadView() {
        setContent {
            AppTheme {
                val cameraViewModel: CameraViewModel = viewModel()
                val cameraState by cameraViewModel.cameraState.collectAsState()
                val uiState = cameraState.uiState
                val listState = rememberLazyListState()
                val context = LocalContext.current
                Scaffold(
                    topBar = {
                        if (uiState == UIState.LOADED) {
                            MainAppBar(cameraViewModel, listState)
                        }
                    },
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        when (uiState) {
                            UIState.INITIAL -> LaunchedEffect(true) { cameraViewModel.downloadAll(context) }
                            UIState.LOADING -> LoadingScreen()
                            UIState.LOADED -> MainContent(cameraViewModel, listState)
                            UIState.ERROR -> ErrorScreen { cameraViewModel.downloadAll(context) }
                        }
                    }
                }

            }
        }
    }

    private fun showCameras(cameras: ArrayList<Camera>, shuffle: Boolean = false) {
        startActivity(Intent(this@MainActivity, CameraActivity::class.java).apply {
            putParcelableArrayListExtra("cameras", cameras)
            putExtra("shuffle", shuffle)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadView()
    }

    private fun showLicences() {
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
    }

    private fun rateApp() {
        val manager = ReviewManagerFactory.create(this@MainActivity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(this@MainActivity, reviewInfo)
            }
            else {
                Log.w("rateApp", task.exception)
            }
        }
    }

    @Composable
    private fun getActions(cameraViewModel: CameraViewModel): List<Action> {
        val cameraState by cameraViewModel.cameraState.collectAsState()
        val selectedCameras = cameraState.selectedCameras
        val context = LocalContext.current
        val clearSelection =
            Action(icon = Icons.Rounded.Clear, toolTip = stringResource(R.string.clear), true, onClick = {
                cameraViewModel.clearSelectedCameras()
            })
        val view = Action(icon = Icons.Rounded.CameraAlt,
            toolTip = stringResource(id = R.string.view),
            selectedCameras.size <= 8,
            onClick = {
                showCameras(cameraViewModel.cameraState.value.selectedCameras as ArrayList<Camera>)
            })
        val allIsFavourite = selectedCameras.all { it.isFavourite }
        val favouriteToolTip = stringResource(
            if (allIsFavourite) {
                R.string.remove_from_favourites
            }
            else {
                R.string.add_to_favourites
            }
        )
        val favouriteIcon = if (allIsFavourite) {
            Icons.Rounded.StarBorder
        }
        else {
            Icons.Rounded.Star
        }
        val favourite = Action(icon = favouriteIcon, toolTip = favouriteToolTip, true, onClick = {
            cameraViewModel.favouriteSelectedCameras(this, !allIsFavourite)
        })
        val allIsHidden = selectedCameras.all { !it.isVisible }
        val hiddenToolTip = stringResource(
            if (allIsHidden) {
                R.string.unhide
            }
            else {
                R.string.hide
            }
        )
        val hiddenIcon = if (allIsHidden) {
            Icons.Rounded.Visibility
        }
        else {
            Icons.Rounded.VisibilityOff
        }
        val hide = Action(icon = hiddenIcon, toolTip = hiddenToolTip, true, onClick = {
            cameraViewModel.hideSelectedCameras(context, allIsHidden)
        })
        val selectAll = Action(icon = Icons.Rounded.SelectAll,
            toolTip = stringResource(R.string.select_all),
            condition = selectedCameras.size < cameraState.displayedCameras.size,
            onClick = {
                cameraViewModel.selectAllCameras()
            })
        val switchView = Action(icon = when (cameraState.viewMode) {
            ViewMode.LIST -> Icons.Rounded.List
            ViewMode.MAP -> Icons.Filled.Place
            else -> Icons.Rounded.GridView
        }, condition = true, isMenu = true, toolTip = stringResource(id = R.string.switch_view), menuContent = {
            var expanded by remember { mutableStateOf(it) }
            ViewModeMenu(expanded = expanded xor it, currentViewMode = cameraState.viewMode) { viewMode ->
                expanded = !expanded
                cameraViewModel.changeViewMode(context, viewMode)
            }
        })
        val sort = Action(icon = Icons.Rounded.Sort,
            condition = cameraState.viewMode != ViewMode.MAP,
            isMenu = true,
            toolTip = stringResource(id = R.string.sort),
            menuContent = {
                var expanded by remember { mutableStateOf(it) }
                SortModeMenu(expanded = expanded xor it, currentSortMode = cameraState.sortMode) { sortMode ->
                    expanded = !expanded
                    cameraViewModel.changeSortMode(context, sortMode)
                }
            })
        val search = Action(icon = Icons.Rounded.Search,
            condition = cameraState.searchMode != SearchMode.NAME,
            toolTip = stringResource(id = R.string.search),
            checked = cameraState.searchMode == SearchMode.NAME,
            onClick = {
                cameraViewModel.changeSearchMode(SearchMode.NAME)
            })
        val searchNeighbourhood = Action(icon = Icons.Rounded.TravelExplore,
            condition = cameraState.searchMode != SearchMode.NEIGHBOURHOOD,
            toolTip = stringResource(id = R.string.search_neighbourhood),
            checked = cameraState.searchMode == SearchMode.NEIGHBOURHOOD,
            onClick = {
                cameraViewModel.changeSearchMode(SearchMode.NEIGHBOURHOOD)
            })
        val favourites = Action(icon = Icons.Rounded.Star,
            condition = true,
            toolTip = stringResource(id = R.string.favourites),
            checked = cameraState.filterMode == FilterMode.FAVOURITE,
            onClick = {
                cameraViewModel.changeFilterMode(FilterMode.FAVOURITE)
            })
        val hidden = Action(icon = Icons.Rounded.VisibilityOff,
            condition = true,
            toolTip = stringResource(id = R.string.hidden_cameras),
            checked = cameraState.filterMode == FilterMode.HIDDEN,
            onClick = {
                cameraViewModel.changeFilterMode(FilterMode.HIDDEN)
            })
        val random = Action(icon = Icons.Rounded.Casino,
            condition = true,
            toolTip = stringResource(id = R.string.random_camera),
            onClick = {
                showCameras(arrayListOf(cameraState.visibleCameras.random()))
            })
        val shuffle = Action(
            icon = Icons.Rounded.Shuffle,
            condition = true,
            toolTip = stringResource(id = R.string.shuffle),
            onClick = {
                showCameras(cameraViewModel.cameraState.value.visibleCameras as ArrayList<Camera>, true)
            })

        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            AboutDialog(onRate = {
                rateApp()
            }, onLicences = {
                showLicences()
            }) {
                showDialog = !showDialog
            }
        }

        val about = Action(
            icon = Icons.Rounded.Info,
            condition = true,
            toolTip = stringResource(id = R.string.about),
            onClick = {
                showDialog = !showDialog
            })

        if (selectedCameras.isEmpty()) {
            return listOf(
                switchView,
                sort,
                search,
                searchNeighbourhood,
                favourites,
                hidden,
                random,
                shuffle,
                about,
            )
        }
        return listOf(
            clearSelection,
            view,
            favourite,
            hide,
            selectAll,
        )
    }

    fun requestLocationPermissions(
        requestCode: Int, onPermissionGranted: ((location: Location) -> Unit) = {},
    ): Boolean {
        val permissionArray = arrayOf(
            permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION
        )
        val noPermissionsGranted = permissionArray.all {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (noPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissionArray, requestCode)
            return false
        }
        else {
            when (requestCode) {
                requestForList -> {
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    return if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && lastLocation != null) {
                        onPermissionGranted(lastLocation)
                        true
                    }
                    else {
                        Snackbar.make(
                            window.decorView.rootView, getString(R.string.location_unavailable), Snackbar.LENGTH_LONG
                        ).show()
                        false
                    }
                }

                requestForMap -> {
                    return true
                }
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PackageManager.PERMISSION_GRANTED in grantResults) {
            requestLocationPermissions(requestCode)
        }
    }
}