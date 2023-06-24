@file:OptIn(ExperimentalMaterial3Api::class)

package com.textfield.json.ottawastreetcameras.activities

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
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.multidex.BuildConfig
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.play.core.review.ReviewManagerFactory
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.SortMode
import com.textfield.json.ottawastreetcameras.ViewMode
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood
import com.textfield.json.ottawastreetcameras.ui.ActionBar
import com.textfield.json.ottawastreetcameras.ui.ActionModeMenu
import com.textfield.json.ottawastreetcameras.ui.AppTheme
import com.textfield.json.ottawastreetcameras.ui.CameraGalleryView
import com.textfield.json.ottawastreetcameras.ui.CameraListView
import com.textfield.json.ottawastreetcameras.ui.NeighbourhoodSearchBar
import com.textfield.json.ottawastreetcameras.ui.SearchBar
import com.textfield.json.ottawastreetcameras.ui.SectionIndex
import com.textfield.json.ottawastreetcameras.ui.StreetCamsMap
import com.textfield.json.ottawastreetcameras.ui.Visibility

class UIStateViewModel : ViewModel() {
    enum class UIStates { LOADING, LOADED, ERROR }

    private var _state: MutableLiveData<UIStates> = MutableLiveData<UIStates>(UIStates.LOADING)
    val state: LiveData<UIStates>
        get() = _state

    fun onStateChanged(state: UIStates) {
        _state.value = state
    }
}

class MainActivity : AppCompatActivity() {
    private var cameras = ArrayList<Camera>()
    private var neighbourhoods = ArrayList<Neighbourhood>()
    private val requestForList = 0
    private val requestForMap = 1
    private val cameraManager = CameraManager.getInstance()
    private val uiStateViewModel = UIStateViewModel()


    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        /*
        if (actionMode == null) {
            selectedCameras.clear()
        }
        */
    }

    private fun shuffleCameras() {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putParcelableArrayListExtra("cameras", cameras)
        intent.putExtra("shuffle", true)
        getResult.launch(intent)
    }

    @OptIn(ExperimentalMaterial3Api::class)
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
                if (cameraManager.getSelectedCameras().isNotEmpty()) {
                    ActionModeMenu() {
                        menuItemClicked(it)
                    }
                } else {
                    ActionBar() {
                        menuItemClicked(it)
                    }
                }
            }
        )
    }

    private fun menuItemClicked(id: Int) {
        when (id) {
            R.string.random_camera -> {
                showCamera(cameras.random())
            }

            R.string.shuffle -> {
                shuffleCameras()
            }

            R.string.about -> {
                showAboutDialog()
            }

            R.string.more -> {}

            else -> {
                loadView()
            }
        }
    }

    @Composable
    fun MainContent(padding: PaddingValues) {
        Column(modifier = Modifier.padding(padding)) {
            val displayedCameras = cameras.filter {
                when (cameraManager.filterMode) {
                    FilterMode.FAVOURITE -> it.isFavourite
                    FilterMode.HIDDEN -> !it.isVisible
                    FilterMode.VISIBLE -> it.isVisible
                }
            } as ArrayList<Camera>
            when (cameraManager.sortMode) {
                SortMode.NAME -> {
                    displayedCameras.sortWith(SortByName())
                }

                SortMode.NEIGHBOURHOOD -> {
                    displayedCameras.sortWith(SortByNeighbourhood())
                }

                SortMode.DISTANCE -> {
                    requestLocationPermissions(requestForList) { lastLocation ->
                        displayedCameras.sortWith(SortByDistance(lastLocation))
                    }
                }
            }
            when (cameraManager.viewMode) {
                ViewMode.LIST -> {
                    Row {
                        Visibility(visible = cameraManager.showSectionIndex) {
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
                    StreetCamsMap(
                        displayedCameras,
                        isMyLocationEnabled = requestLocationPermissions(requestForMap),
                    ) { showCamera(it) }
                }

                ViewMode.GALLERY -> {
                    CameraGalleryView(displayedCameras) { showCamera(it) }
                }
            }
        }
    }

    private fun loadView() {
        setContent {
            AppTheme {
                val uiState = uiStateViewModel.state.observeAsState()
                when (uiState.value) {
                    UIStateViewModel.UIStates.LOADING -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    UIStateViewModel.UIStates.LOADED -> {
                        Scaffold(
                            topBar = {
                                MainAppBar()
                            },
                            content = {
                                MainContent(it)
                            },
                        )
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center,
                                text = "Error loading data",
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showCamera(camera: Camera) {
        val intent = Intent(this@MainActivity, CameraActivity::class.java)
        intent.putParcelableArrayListExtra("cameras", arrayListOf(camera))
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
            .setNegativeButton("Close") { _, _ -> }
            .setPositiveButton(R.string.rate) { _, _ -> rateApp() }
            .setNeutralButton(R.string.licences) { _, _ ->
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
        loadView()
        cameraManager.downloadAll(this) { cameras, neighbourhoods ->
            this.cameras = cameras
            this.neighbourhoods = neighbourhoods
            uiStateViewModel.onStateChanged(UIStateViewModel.UIStates.LOADED)
        }
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
        requestCode: Int,
        onPermissionGranted: ((location: Location) -> Unit)? = null
    ): Boolean {
        val permissionArray = arrayOf(
            permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_COARSE_LOCATION
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