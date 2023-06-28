@file:OptIn(ExperimentalMaterial3Api::class)

package com.textfield.json.ottawastreetcameras.ui.activities

import android.Manifest.permission
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.multidex.BuildConfig
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.play.core.review.ReviewManagerFactory
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.SortMode
import com.textfield.json.ottawastreetcameras.UIStates
import com.textfield.json.ottawastreetcameras.ViewMode
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.components.ActionBar
import com.textfield.json.ottawastreetcameras.ui.components.ActionModeMenu
import com.textfield.json.ottawastreetcameras.ui.components.AppTheme
import com.textfield.json.ottawastreetcameras.ui.components.CameraGalleryView
import com.textfield.json.ottawastreetcameras.ui.components.CameraListView
import com.textfield.json.ottawastreetcameras.ui.components.NeighbourhoodSearchBar
import com.textfield.json.ottawastreetcameras.ui.components.SearchBar
import com.textfield.json.ottawastreetcameras.ui.components.SectionIndex
import com.textfield.json.ottawastreetcameras.ui.components.StreetCamsMap
import com.textfield.json.ottawastreetcameras.ui.components.Visibility

class MainActivity : AppCompatActivity() {
    private val requestForList = 0
    private val requestForMap = 1
    private val cameraManager = CameraManager.getInstance()

    private fun shuffleCameras() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putParcelableArrayListExtra("cameras", cameraManager.visibleCameras as ArrayList<Camera>)
        intent.putExtra("shuffle", true)
        startActivity(intent)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainAppBar() {
        val cameras = cameraManager.displayedCameras
        val searchMode = cameraManager.searchMode.observeAsState()
        TopAppBar(title = {
            when (searchMode.value) {
                SearchMode.NONE -> {
                    Text(stringResource(R.string.app_name))
                }

                SearchMode.NAME -> {
                    SearchBar(
                        pluralStringResource(
                            R.plurals.search_hint, cameras.size, cameras.size
                        )
                    ) {
                        cameraManager.searchDisplayedCameras(SearchMode.NAME, it)
                    }
                }

                SearchMode.NEIGHBOURHOOD -> {
                    NeighbourhoodSearchBar(
                        pluralStringResource(
                            R.plurals.search_hint_neighbourhood,
                            cameraManager.neighbourhoods.size,
                            cameraManager.neighbourhoods.size
                        )
                    ) {
                        cameraManager.searchDisplayedCameras(SearchMode.NEIGHBOURHOOD, it)
                    }
                }

                else -> {}
            }
        }, actions = {
            if (cameraManager.getSelectedCameras().isNotEmpty()) {
                ActionModeMenu {
                    menuItemClicked(it)
                }
            } else {
                ActionBar {
                    menuItemClicked(it)
                }
            }
        })
    }

    private fun menuItemClicked(id: Int) {
        when (id) {
            R.string.random_camera -> {
                showCamera(cameraManager.visibleCameras.random())
            }

            R.string.shuffle -> {
                shuffleCameras()
            }

            R.string.about -> {
                showAboutDialog()
            }

            R.string.more -> {}

            else -> {
            }
        }
    }

    @Composable
    fun MainContent(padding: PaddingValues) {
        Column(modifier = Modifier.padding(padding)) {
            var displayedCameras = cameraManager.displayedCameras

            val filterMode = cameraManager.filterMode.observeAsState()
            displayedCameras = displayedCameras.filter {
                when (filterMode.value) {
                    FilterMode.VISIBLE -> it.isVisible
                    FilterMode.HIDDEN -> !it.isVisible
                    FilterMode.FAVOURITE -> it.isFavourite
                    else -> true
                }
            } as ArrayList<Camera>

            val sortMode = cameraManager.sortMode.observeAsState()
            when (sortMode.value) {
                SortMode.NAME -> {
                    displayedCameras.sortWith(SortByName())
                }
                SortMode.DISTANCE -> {
                    requestLocationPermissions(requestForList) {
                        displayedCameras.sortWith(SortByDistance(it))
                    }
                }
                SortMode.NEIGHBOURHOOD -> {
                    displayedCameras.sortWith(SortByNeighbourhood())
                }
                else -> {}
            }

            val viewMode = cameraManager.viewMode.observeAsState()
            when (viewMode.value) {
                ViewMode.LIST -> {
                    Row {
                        Visibility(visible = cameraManager.showSectionIndex) {
                            SectionIndex(displayedCameras)
                        }
                        CameraListView(
                            displayedCameras, onItemClick = { showCamera(it) }, modifier = Modifier.weight(1f)
                        )
                    }
                }

                ViewMode.MAP -> {
                    StreetCamsMap(
                        displayedCameras,
                        isMyLocationEnabled = requestLocationPermissions(requestForMap),
                    ) { showCamera(it) }
                }

                ViewMode.GALLERY -> {
                    CameraGalleryView(displayedCameras) { showCamera(it) }
                }

                else -> {}
            }
        }
    }

    private fun loadView() {
        setContent {
            AppTheme {
                val uiState = cameraManager.state.observeAsState()
                when (uiState.value) {
                    UIStates.LOADING -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    UIStates.LOADED -> {
                        Scaffold(
                            topBar = {
                                MainAppBar()
                            },
                            content = {
                                MainContent(it)
                            },
                        )
                    }

                    UIStates.ERROR -> {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center,
                                text = "Error loading data",
                                color = Color.White
                            )
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

    private fun showAboutDialog() {
        AlertDialog.Builder(this).setTitle(resources.getString(R.string.app_name_long)).setMessage(
            resources.getString(
                R.string.version, BuildConfig.VERSION_NAME
            )
        ).setNegativeButton("Close") { _, _ -> }.setPositiveButton(R.string.rate) { _, _ -> rateApp() }
            .setNeutralButton(R.string.licences) { _, _ ->
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadView()
        cameraManager.downloadAll(this)
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

    private fun requestLocationPermissions(
        requestCode: Int, onPermissionGranted: ((location: Location) -> Unit)? = null
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
        } else {
            when (requestCode) {
                requestForList -> {
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && lastLocation != null) {
                        onPermissionGranted?.invoke(lastLocation)
                        return true
                    } else {
                        //Snackbar.make(listView, getString(R.string.location_unavailable), Snackbar.LENGTH_LONG).show()
                        return false
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