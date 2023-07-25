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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import com.textfield.json.ottawastreetcameras.CameraManager
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
import com.textfield.json.ottawastreetcameras.ui.components.SectionIndex
import com.textfield.json.ottawastreetcameras.ui.components.StreetCamsMap
import com.textfield.json.ottawastreetcameras.ui.components.menu.Action
import com.textfield.json.ottawastreetcameras.ui.components.menu.ActionBar
import com.textfield.json.ottawastreetcameras.ui.components.menu.SortModeMenu
import com.textfield.json.ottawastreetcameras.ui.components.menu.ViewModeMenu

class MainActivity : AppCompatActivity() {
    private val requestForList = 0
    private val requestForMap = 1
    private val cameraManager = CameraManager.getInstance()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainAppBar(listState: LazyListState, useDarkTheme: Boolean = isSystemInDarkTheme()) {
        val cameraState = cameraManager.cameraState.collectAsState()
        TopAppBar(navigationIcon = {
            if (cameraState.value.selectedCameras.isEmpty()) {
                if (cameraState.value.searchMode != SearchMode.NONE) {
                    IconButton(onClick = {
                        cameraManager.changeSearchMode(SearchMode.NONE)
                        cameraManager.searchCameras("")
                    }) {
                        Icon(Icons.Rounded.ArrowBack, stringResource(id = R.string.back), tint = Color.White)
                    }
                }
                else if (cameraState.value.filterMode != FilterMode.VISIBLE) {
                    IconButton(onClick = {
                        cameraManager.changeFilterMode(FilterMode.VISIBLE)
                    }) {
                        Icon(Icons.Rounded.ArrowBack, stringResource(id = R.string.back), tint = Color.White)
                    }
                }
            }
        }, title = {
            AppBarTitle(listState)
        }, actions = {
            ActionBar(getActions()) {
            }
        },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (!useDarkTheme || cameraState.value.selectedCameras.isNotEmpty()) {
                    colorResource(id = R.color.colorAccent)
                }
                else {
                    Color.Black
                }
            )
        )
    }

    @Composable
    fun MainContent(padding: PaddingValues, listState: LazyListState) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(padding)) {
                val cameraState = cameraManager.cameraState.collectAsState()
                val displayedCameras = cameraState.value.displayedCameras
                val onItemClick = { camera: Camera ->
                    if (cameraState.value.selectedCameras.isNotEmpty()) {
                        cameraManager.selectCamera(camera)
                    }
                    else {
                        showCamera(camera)
                    }
                }
                val onItemLongClick = { camera: Camera -> cameraManager.selectCamera(camera) }
                when (cameraState.value.viewMode) {
                    ViewMode.LIST -> {
                        Row {
                            AnimatedVisibility(visible = cameraState.value.showSectionIndex) {
                                SectionIndex(displayedCameras, listState)
                            }
                            CameraListView(
                                displayedCameras,
                                modifier = Modifier.weight(1f),
                                listState = listState,
                                onItemClick = onItemClick,
                                onItemLongClick = onItemLongClick
                            )
                        }
                    }

                    ViewMode.MAP -> {
                        StreetCamsMap(
                            displayedCameras,
                            isMyLocationEnabled = requestLocationPermissions(requestForMap),
                            onItemClick = onItemClick,
                            onItemLongClick = onItemLongClick
                        )
                    }

                    ViewMode.GALLERY -> {
                        CameraGalleryView(
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
                val cameraState = cameraManager.cameraState.collectAsState()
                val listState = remember { LazyListState() }
                val context = LocalContext.current
                Scaffold(topBar = {
                    if (cameraState.value.uiState == UIState.LOADED) {
                        MainAppBar(listState)
                    }
                }) {
                    when (cameraState.value.uiState) {
                        UIState.LOADING -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }

                        UIState.LOADED -> {
                            MainContent(it, listState)
                        }

                        UIState.ERROR -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it)
                            ) {
                                Column(
                                    modifier = Modifier.align(Alignment.Center)
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.could_not_load_long),
                                    )
                                    IconButton(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(10.dp),
                                        onClick = {
                                            cameraManager.downloadAll(context)
                                        }
                                    ) {
                                        Icon(Icons.Rounded.Refresh, stringResource(R.string.retry))
                                    }
                                }
                            }
                        }

                        UIState.INITIAL -> {}
                    }
                }

            }
        }
    }

    private fun shuffleCameras() {
        val intent = Intent(this@MainActivity, CameraActivity::class.java)
        intent.putParcelableArrayListExtra(
            "cameras",
            cameraManager.cameraState.value.visibleCameras as ArrayList<Camera>
        )
        intent.putExtra("shuffle", true)
        startActivity(intent)
    }

    private fun showCamera(camera: Camera) {
        val intent = Intent(this@MainActivity, CameraActivity::class.java)
        intent.putParcelableArrayListExtra("cameras", arrayListOf(camera))
        startActivity(intent)
    }

    private fun showSelectedCameras() {
        val intent = Intent(this@MainActivity, CameraActivity::class.java)
        intent.putParcelableArrayListExtra(
            "cameras",
            cameraManager.cameraState.value.selectedCameras as ArrayList<Camera>
        )
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadView()
        cameraManager.downloadAll(this)
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
    private fun getActions(): List<Action> {
        val cameraState = cameraManager.cameraState.collectAsState()
        val selectedCameras = cameraState.value.selectedCameras
        val context = LocalContext.current
        val clearSelection = Action(
            icon = Icons.Rounded.Clear,
            toolTip = stringResource(R.string.clear),
            true,
            onClick = {
                cameraManager.clearSelectedCameras()
            }
        )
        val view = Action(
            icon = Icons.Rounded.CameraAlt,
            toolTip = stringResource(id = R.string.show),
            selectedCameras.size <= 8,
            onClick = {
                showSelectedCameras()
            }
        )
        val allIsFavourite = selectedCameras.all { it.isFavourite }
        val favouriteToolTip = stringResource(
            if (allIsFavourite) {
                R.string.remove_from_favourites
            }
            else {
                R.string.add_to_favourites
            }
        )
        val favouriteIcon =
            if (allIsFavourite) {
                Icons.Rounded.StarBorder
            }
            else {
                Icons.Rounded.Star
            }
        val favourite = Action(
            icon = favouriteIcon,
            toolTip = favouriteToolTip,
            true,
            onClick = {
                cameraManager.favouriteSelectedCameras(this, !allIsFavourite)
            }
        )
        val allIsHidden = selectedCameras.all { !it.isVisible }
        val hiddenToolTip = stringResource(
            if (allIsHidden) {
                R.string.unhide
            }
            else {
                R.string.hide
            }
        )
        val hiddenIcon =
            if (allIsHidden) {
                Icons.Rounded.Visibility
            }
            else {
                Icons.Rounded.VisibilityOff
            }
        val hide = Action(icon = hiddenIcon,
            toolTip = hiddenToolTip,
            true,
            onClick = {
                cameraManager.hideSelectedCameras(context, allIsHidden)
            }
        )
        val selectAll = Action(
            icon = Icons.Rounded.SelectAll,
            toolTip = stringResource(R.string.select_all),
            condition = selectedCameras.size < cameraState.value.displayedCameras.size,
            onClick = {
                cameraManager.selectAllCameras()
            }
        )
        val switchView = Action(
            icon = when (cameraState.value.viewMode) {
                ViewMode.LIST -> Icons.Rounded.List
                ViewMode.MAP -> Icons.Filled.Place
                else -> Icons.Rounded.GridView
            },
            condition = true,
            isMenu = true,
            toolTip = stringResource(id = R.string.switch_view),
            menuContent = {
                var expanded by remember { mutableStateOf(it) }
                ViewModeMenu(expanded xor it) {
                    expanded = !expanded
                }
            }
        )
        val sort = Action(
            icon = Icons.Rounded.Sort,
            condition = cameraState.value.viewMode != ViewMode.MAP,
            isMenu = true,
            toolTip = stringResource(id = R.string.sort),
            menuContent = {
                var expanded by remember { mutableStateOf(it) }
                SortModeMenu(expanded xor it) {
                    expanded = !expanded
                }
            }
        )
        val search = Action(
            icon = Icons.Rounded.Search,
            condition = cameraState.value.searchMode != SearchMode.NAME,
            toolTip = stringResource(id = R.string.search),
            checked = cameraState.value.searchMode == SearchMode.NAME,
            onClick = {
                cameraManager.changeSearchMode(SearchMode.NAME)
            }
        )
        val searchNeighbourhood = Action(
            icon = Icons.Rounded.TravelExplore,
            condition = cameraState.value.searchMode != SearchMode.NEIGHBOURHOOD,
            toolTip = stringResource(id = R.string.search_neighbourhood),
            checked = cameraState.value.searchMode == SearchMode.NEIGHBOURHOOD,
            onClick = {
                cameraManager.changeSearchMode(SearchMode.NEIGHBOURHOOD)
            }
        )
        val favourites = Action(
            icon = Icons.Rounded.Star,
            condition = true,
            toolTip = stringResource(id = R.string.favourites),
            checked = cameraState.value.filterMode == FilterMode.FAVOURITE,
            onClick = {
                cameraManager.changeFilterMode(FilterMode.FAVOURITE)
            }
        )
        val hidden = Action(
            icon = Icons.Rounded.VisibilityOff,
            condition = true,
            toolTip = stringResource(id = R.string.hidden_cameras),
            checked = cameraState.value.filterMode == FilterMode.HIDDEN,
            onClick = {
                cameraManager.changeFilterMode(FilterMode.HIDDEN)
            }
        )
        val random = Action(
            icon = Icons.Rounded.Casino,
            condition = true,
            toolTip = stringResource(id = R.string.random_camera),
            onClick = {
                showCamera(cameraState.value.visibleCameras.random())
            }
        )
        val shuffle = Action(
            icon = Icons.Rounded.Shuffle,
            condition = true,
            toolTip = stringResource(id = R.string.shuffle),
            onClick = {
                shuffleCameras()
            }
        )

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
            }
        )

        if (selectedCameras.isEmpty()) {
            return listOf<Action>(
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
        return listOf<Action>(
            clearSelection,
            view,
            favourite,
            hide,
            selectAll,
        )
    }

    fun requestLocationPermissions(
        requestCode: Int, onPermissionGranted: ((location: Location) -> Unit)? = null,
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
                        onPermissionGranted?.invoke(lastLocation)
                        true
                    }
                    else {
                        Snackbar.make(
                            window.decorView.rootView,
                            getString(R.string.location_unavailable),
                            Snackbar.LENGTH_LONG
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