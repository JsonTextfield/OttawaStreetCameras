package com.textfield.json.ottawastreetcameras.ui.activities

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.DropdownMenu
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
import androidx.core.content.ContextCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.SortMode
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
import com.textfield.json.ottawastreetcameras.ui.components.menu.Action
import com.textfield.json.ottawastreetcameras.ui.components.menu.ActionBar
import com.textfield.json.ottawastreetcameras.ui.components.menu.RadioMenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainAppBar(cameraViewModel: CameraViewModel, listState: LazyListState, gridState: LazyGridState) {
        val cameraState by cameraViewModel.cameraState.collectAsState()
        TopAppBar(
            navigationIcon = {
                if (cameraState.showBackButton) {
                    IconButton(onClick = {
                        cameraViewModel.resetFilters()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            stringResource(id = R.string.back),
                            tint = Color.White
                        )
                    }
                }
            },
            title = {
                AppBarTitle(cameraViewModel) {
                    CoroutineScope(Dispatchers.Main).launch {
                        when (cameraState.viewMode) {
                            ViewMode.LIST -> listState.scrollToItem(0)
                            ViewMode.GALLERY -> gridState.scrollToItem(0)
                            else -> {}
                        }
                    }
                }
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
    fun MainContent(cameraViewModel: CameraViewModel, listState: LazyListState, gridState: LazyGridState) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column {
                val cameraState by cameraViewModel.cameraState.collectAsState()
                val onItemClick = { camera: Camera ->
                    if (cameraState.selectedCameras.isNotEmpty()) {
                        cameraViewModel.selectCamera(camera)
                    }
                    else {
                        showCameras(arrayListOf(camera))
                    }
                }
                val onItemLongClick = { camera: Camera -> cameraViewModel.selectCamera(camera) }
                val onItemDismissed = { camera: Camera ->
                    cameraViewModel.hideCameras(listOf(camera))
                    showUndoSnackbar(camera) {
                        cameraViewModel.hideCameras(listOf(camera))
                    }
                }
                when (cameraState.viewMode) {
                    ViewMode.LIST -> {
                        CameraListView(
                            cameraViewModel,
                            listState = listState,
                            onItemClick = onItemClick,
                            onItemLongClick = onItemLongClick,
                            onItemDismissed = onItemDismissed,
                        )
                    }

                    ViewMode.MAP -> {
                        CameraMapView(
                            cameraViewModel,
                            onItemClick = onItemClick,
                            onItemLongClick = onItemLongClick
                        )
                    }

                    ViewMode.GALLERY -> {
                        CameraGalleryView(
                            cameraViewModel,
                            gridState = gridState,
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
                val cameraViewModel: CameraViewModel by viewModels { CameraViewModel.CameraViewModelFactory }
                val cameraState by cameraViewModel.cameraState.collectAsState()
                val uiState = cameraState.uiState
                val listState = rememberLazyListState()
                val gridState = rememberLazyGridState()
                val context = LocalContext.current
                Scaffold(
                    topBar = {
                        if (uiState == UIState.LOADED) {
                            MainAppBar(cameraViewModel, listState, gridState)
                        }
                    },
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        when (uiState) {
                            UIState.INITIAL -> LaunchedEffect(true) { cameraViewModel.download(context) }
                            UIState.LOADING -> LoadingScreen()
                            UIState.LOADED -> MainContent(cameraViewModel, listState, gridState)
                            UIState.ERROR -> ErrorScreen { cameraViewModel.download(context) }
                        }
                    }
                }
            }
        }
    }

    private fun showUndoSnackbar(camera: Camera, callback: () -> Unit) {
        val visibilityStringId = if (camera.isVisible) R.string.unhidden else R.string.hidden
        Snackbar.make(
            window.decorView.rootView,
            getString(R.string.camera_visibility_changed, camera.name, getString(visibilityStringId)),
            Snackbar.LENGTH_LONG
        ).setAction(R.string.undo) {
            callback()
        }.show()
    }

    private fun showCameras(cameras: List<Camera>, shuffle: Boolean = false) {
        startActivity(Intent(this@MainActivity, CameraActivity::class.java).apply {
            putParcelableArrayListExtra("cameras", cameras as ArrayList<Camera>)
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

    @Composable
    private fun getActions(cameraViewModel: CameraViewModel): List<Action> {
        val cameraState by cameraViewModel.cameraState.collectAsState()
        val selectedCameras = cameraState.selectedCameras
        val clearSelection = Action(
            icon = Icons.Rounded.Clear, tooltip = stringResource(R.string.clear), true,
            onClick = { cameraViewModel.selectAllCameras(false) },
        )
        val view = Action(
            icon = Icons.Rounded.CameraAlt,
            tooltip = stringResource(id = R.string.view),
            selectedCameras.size <= 8,
            onClick = { showCameras(cameraViewModel.cameraState.value.selectedCameras) },
        )
        val allIsFavourite = selectedCameras.all { it.isFavourite }
        val favourite = Action(
            icon = if (allIsFavourite) Icons.Rounded.StarBorder else Icons.Rounded.Star,
            tooltip = stringResource(
                if (allIsFavourite) {
                    R.string.remove_from_favourites
                }
                else {
                    R.string.add_to_favourites
                }
            ),
            onClick = { cameraViewModel.favouriteCameras(cameraState.selectedCameras) },
        )
        val allIsHidden = selectedCameras.all { !it.isVisible }
        val hide = Action(
            icon = if (allIsHidden) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
            tooltip = stringResource(if (allIsHidden) R.string.unhide else R.string.hide),
            onClick = { cameraViewModel.hideCameras(cameraState.selectedCameras) },
        )
        val selectAll = Action(
            icon = Icons.Rounded.SelectAll,
            tooltip = stringResource(R.string.select_all),
            isVisible = selectedCameras.size < cameraState.displayedCameras.size,
            onClick = { cameraViewModel.selectAllCameras() },
        )
        val switchView = Action(
            icon = when (cameraState.viewMode) {
                ViewMode.LIST -> Icons.AutoMirrored.Rounded.List
                ViewMode.MAP -> Icons.Filled.Place
                ViewMode.GALLERY -> Icons.Rounded.GridView
            },
            tooltip = stringResource(
                id = when (cameraState.viewMode) {
                    ViewMode.LIST -> R.string.list
                    ViewMode.MAP -> R.string.map
                    ViewMode.GALLERY -> R.string.gallery
                }
            ),
            menuContent = {
                var expanded by remember { mutableStateOf(it) }
                DropdownMenu(
                    expanded = expanded xor it,
                    onDismissRequest = { expanded = !expanded },
                ) {
                    ViewMode.entries.forEach { viewMode ->
                        RadioMenuItem(
                            title = stringResource(viewMode.key),
                            selected = cameraState.viewMode == viewMode,
                            onClick = {
                                expanded = !expanded
                                cameraViewModel.changeViewMode(viewMode)
                            },
                        )
                    }
                }
            },
        )
        val sort = Action(
            icon = Icons.AutoMirrored.Rounded.Sort,
            isVisible = cameraState.viewMode != ViewMode.MAP,
            tooltip = stringResource(id = R.string.sort),
            menuContent = {
                val permissionArray = arrayOf(
                    permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION
                )

                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { permissions ->
                        val noPermissionsGranted = permissionArray.all { permission ->
                            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
                        }

                        if (!noPermissionsGranted) {
                            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (lastLocation != null) {
                                cameraViewModel.changeSortMode(SortMode.DISTANCE, lastLocation)
                            }
                            else {
                                Snackbar.make(
                                    window.decorView.rootView,
                                    getString(R.string.location_unavailable),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                )

                var expanded by remember { mutableStateOf(it) }
                DropdownMenu(
                    expanded = expanded xor it,
                    onDismissRequest = { expanded = !expanded },
                ) {
                    SortMode.entries.forEach { sortMode ->
                        RadioMenuItem(
                            title = stringResource(sortMode.key),
                            selected = cameraState.sortMode == sortMode,
                            onClick = {
                                expanded = !expanded
                                if (sortMode == SortMode.DISTANCE) {
                                    locationPermissionLauncher.launch(permissionArray)
                                }
                                else {
                                    cameraViewModel.changeSortMode(sortMode)
                                }
                            },
                        )
                    }
                }
            },
        )
        val search = Action(
            icon = Icons.Rounded.Search,
            isVisible = cameraState.searchMode != SearchMode.NAME,
            tooltip = stringResource(id = R.string.search),
            isChecked = cameraState.searchMode == SearchMode.NAME,
            onClick = { cameraViewModel.searchCameras(SearchMode.NAME) },
        )
        val searchNeighbourhood = Action(
            icon = Icons.Rounded.TravelExplore,
            isVisible = cameraState.showSearchNeighbourhood,
            tooltip = stringResource(id = R.string.search_neighbourhood),
            isChecked = cameraState.searchMode == SearchMode.NEIGHBOURHOOD,
            onClick = { cameraViewModel.searchCameras(SearchMode.NEIGHBOURHOOD) },
        )
        val favourites = Action(
            icon = Icons.Rounded.Star,
            tooltip = stringResource(id = R.string.favourites),
            isChecked = cameraState.filterMode == FilterMode.FAVOURITE,
            onClick = { cameraViewModel.changeFilterMode(FilterMode.FAVOURITE) },
        )
        val hidden = Action(
            icon = Icons.Rounded.VisibilityOff,
            tooltip = stringResource(id = R.string.hidden_cameras),
            isChecked = cameraState.filterMode == FilterMode.HIDDEN,
            onClick = { cameraViewModel.changeFilterMode(FilterMode.HIDDEN) },
        )
        val random = Action(
            icon = Icons.Rounded.Casino,
            tooltip = stringResource(id = R.string.random_camera),
            onClick = { showCameras(arrayListOf(cameraState.visibleCameras.random())) },
        )
        val shuffle = Action(
            icon = Icons.Rounded.Shuffle,
            tooltip = stringResource(id = R.string.shuffle),
            onClick = {
                showCameras(cameraViewModel.cameraState.value.visibleCameras as ArrayList<Camera>, true)
            },
        )

        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            AboutDialog(
                onLicences = { showLicences() },
                onDismiss = { showDialog = !showDialog },
            )
        }

        val about = Action(
            icon = Icons.Rounded.Info,
            tooltip = stringResource(id = R.string.about),
            onClick = { showDialog = !showDialog },
        )

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
}