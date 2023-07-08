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
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Deselect
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TravelExplore
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
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
import com.textfield.json.ottawastreetcameras.ui.components.Action
import com.textfield.json.ottawastreetcameras.ui.components.ActionBar
import com.textfield.json.ottawastreetcameras.ui.components.ActionModeMenu
import com.textfield.json.ottawastreetcameras.ui.components.AppTheme
import com.textfield.json.ottawastreetcameras.ui.components.CameraGalleryView
import com.textfield.json.ottawastreetcameras.ui.components.CameraListView
import com.textfield.json.ottawastreetcameras.ui.components.SearchBar
import com.textfield.json.ottawastreetcameras.ui.components.SectionIndex
import com.textfield.json.ottawastreetcameras.ui.components.StreetCamsMap
import com.textfield.json.ottawastreetcameras.ui.components.Visibility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val requestForList = 0
    private val requestForMap = 1
    private val cameraManager = CameraManager.getInstance()

    private fun shuffleCameras() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putParcelableArrayListExtra(
            "cameras",
            cameraManager.cameraState.value?.visibleCameras as ArrayList<Camera>
        )
        intent.putExtra("shuffle", true)
        startActivity(intent)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainAppBar(listState: LazyListState, useDarkTheme: Boolean = isSystemInDarkTheme()) {
        val cameraState = cameraManager.cameraState.observeAsState()
        TopAppBar(title = {
            if (cameraState.value?.selectedCameras?.isNotEmpty() == true) {
                Text(
                    pluralStringResource(
                        R.plurals.selectedCameras,
                        cameraState.value?.selectedCameras?.size ?: 0,
                        cameraState.value?.selectedCameras?.size ?: 0
                    ), color = Color.White, modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            CoroutineScope(Dispatchers.Main).launch {
                                listState.scrollToItem(0, 0)
                            }
                        })
            }
            else {
                when (cameraState.value?.searchMode) {
                    SearchMode.NONE -> {
                        Text(stringResource(R.string.app_name), color = Color.White, modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                CoroutineScope(Dispatchers.Main).launch {
                                    listState.scrollToItem(0, 0)
                                }
                            })
                    }

                    SearchMode.NAME -> {
                        SearchBar(
                            pluralStringResource(
                                R.plurals.search_hint,
                                cameraState.value?.displayedCameras?.size ?: 0,
                                cameraState.value?.displayedCameras?.size ?: 0
                            )
                        ) {
                            cameraManager.searchCameras(it)
                        }
                    }

                    SearchMode.NEIGHBOURHOOD -> {
                        SearchBar(
                            pluralStringResource(
                                R.plurals.search_hint_neighbourhood,
                                cameraState.value?.neighbourhoods?.size ?: 0,
                                cameraState.value?.neighbourhoods?.size ?: 0,
                            )
                        ) {
                            cameraManager.searchCameras(it)
                        }
                    }

                    else -> {}
                }
            }
        }, actions = {
            val context = LocalContext.current
            if (cameraState.value?.selectedCameras?.isNotEmpty() == true) {
                val clearSelection =
                    Action(
                        icon = Icons.Rounded.Deselect,
                        toolTip = stringResource(R.string.clear),
                        true,
                        onClick = {
                            cameraManager.clearSelectedCameras()
                        }
                    )
                val view = Action(
                    icon = Icons.Rounded.CameraAlt,
                    toolTip = stringResource(id = R.string.show),
                    (cameraState.value?.selectedCameras?.size ?: 0) <= 8,
                    onClick = {
                        showSelectedCameras()
                    }
                )
                val favourite = Action(
                    icon = Icons.Rounded.Star,
                    toolTip = stringResource(id = R.string.add_to_favourites),
                    true,
                    onClick = {
                        cameraManager.favouriteSelectedCameras(context)
                    }
                )
                val hide = Action(icon = Icons.Rounded.VisibilityOff,
                    toolTip = stringResource(R.string.hide),
                    true,
                    onClick = {
                        cameraManager.hideSelectedCameras(context)
                    }
                )
                val selectAll = Action(
                    icon = Icons.Rounded.SelectAll,
                    toolTip = stringResource(R.string.select_all),
                    condition = (cameraState.value?.selectedCameras?.size
                                 ?: 0) < (cameraState.value?.displayedCameras?.size ?: 0),
                    onClick = {
                        cameraManager.selectAllCameras()
                    }
                )
                val actionModeActions = listOf<Action>(
                    clearSelection,
                    view,
                    favourite,
                    hide,
                    selectAll,
                )
                ActionModeMenu(actionModeActions) {
                }
            }
            else {
                val switchView = Action(
                    icon = when (cameraState.value?.viewMode) {
                        ViewMode.LIST -> Icons.Rounded.List
                        ViewMode.MAP -> Icons.Filled.Place
                        else -> Icons.Rounded.GridView
                    },
                    condition = true,
                    isMenu = true,
                    toolTip = stringResource(id = R.string.switch_view),
                )
                val sort = Action(
                    icon = Icons.Rounded.Sort,
                    condition = cameraState.value?.viewMode != ViewMode.MAP,
                    isMenu = true,
                    toolTip = stringResource(id = R.string.sort),
                )
                val search = Action(
                    icon = Icons.Rounded.Search,
                    condition = true,
                    toolTip = stringResource(id = R.string.search),
                    checked = cameraState.value?.searchMode == SearchMode.NAME,
                    onClick = {
                        cameraManager.changeSearchMode(SearchMode.NAME)
                    }
                )
                val searchNeighbourhood = Action(
                    icon = Icons.Rounded.TravelExplore,
                    condition = true,
                    toolTip = stringResource(id = R.string.search_neighbourhood),
                    checked = cameraState.value?.searchMode == SearchMode.NEIGHBOURHOOD,
                    onClick = {
                        cameraManager.changeSearchMode(SearchMode.NEIGHBOURHOOD)
                    }
                )
                val favourites = Action(
                    icon = Icons.Rounded.Star,
                    condition = true,
                    toolTip = stringResource(id = R.string.favourites),
                    checked = cameraState.value?.filterMode == FilterMode.FAVOURITE,
                    onClick = {
                        cameraManager.changeFilterMode(FilterMode.FAVOURITE)
                    }
                )
                val hidden = Action(
                    icon = Icons.Rounded.VisibilityOff,
                    condition = true,
                    toolTip = stringResource(id = R.string.hidden_cameras),
                    checked = cameraState.value?.filterMode == FilterMode.HIDDEN,
                    onClick = {
                        cameraManager.changeFilterMode(FilterMode.HIDDEN)
                    }
                )
                findViewById<View>(R.id.background)
                val random = Action(
                    icon = Icons.Rounded.Casino,
                    condition = true,
                    toolTip = stringResource(id = R.string.random_camera),
                    onClick = {
                        if (cameraState.value?.displayedCameras != null) {
                            showCamera(cameraState.value?.displayedCameras!!.random())
                        }
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
                val actions = listOf<Action>(
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
                ActionBar(actions) {
                }
            }
        },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (!useDarkTheme || cameraState.value?.selectedCameras?.isNotEmpty() == true) {
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
                val cameraState = cameraManager.cameraState.observeAsState()
                val displayedCameras = cameraState.value?.displayedCameras
                when (cameraState.value?.viewMode) {
                    ViewMode.LIST -> {
                        Row {
                            Visibility(visible = cameraState.value?.showSectionIndex == true) {
                                SectionIndex(displayedCameras ?: ArrayList<Camera>())
                            }
                            CameraListView(
                                displayedCameras ?: ArrayList<Camera>(),
                                modifier = Modifier.weight(1f),
                                listState = listState,
                            ) { showCamera(it) }
                        }
                    }

                    ViewMode.MAP -> {
                        StreetCamsMap(
                            displayedCameras ?: ArrayList<Camera>(),
                            isMyLocationEnabled = requestLocationPermissions(requestForMap),
                        ) { showCamera(it) }
                    }

                    ViewMode.GALLERY -> {
                        CameraGalleryView(displayedCameras ?: ArrayList<Camera>()) { showCamera(it) }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun loadView() {
        setContent {
            AppTheme {
                val cameraState = cameraManager.cameraState.observeAsState()
                when (cameraState.value?.uiState) {
                    UIState.LOADING -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }

                    UIState.LOADED -> {
                        val listState = remember { LazyListState() }
                        Scaffold(
                            modifier = Modifier.padding(0.dp),
                            topBar = {
                                MainAppBar(listState)
                            },
                            content = {
                                MainContent(it, listState)
                            },
                        )
                    }

                    UIState.ERROR -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = "Error loading data",
                                color = Color.White
                            )
                            IconButton(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                                cameraManager.downloadAll(this@MainActivity)
                            }) {
                                Icon(Icons.Rounded.Refresh, "Refresh", tint = Color.White)
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
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
            cameraManager.cameraState.value?.selectedCameras as ArrayList<Camera>
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

    private fun requestLocationPermissions(
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