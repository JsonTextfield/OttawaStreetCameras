package com.textfield.json.ottawastreetcameras.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.network.CameraDownloadService
import com.textfield.json.ottawastreetcameras.network.CameraDownloadServiceImpl
import com.textfield.json.ottawastreetcameras.ui.activities.CameraActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(
    private val _cameraState: MutableStateFlow<CameraState> = MutableStateFlow(CameraState()),
    private val downloadService: CameraDownloadService = CameraDownloadServiceImpl,
    private val prefs: SharedPreferences,
) : ViewModel() {

    val cameraState: StateFlow<CameraState> get() = _cameraState.asStateFlow()

    fun changeViewMode(viewMode: ViewMode) {
        prefs.edit { putString("viewMode", viewMode.name) }
        _cameraState.update { it.copy(viewMode = viewMode) }
    }

    fun changeSortMode(sortMode: SortMode, location: Location? = null) {
        _cameraState.update {
            it.copy(
                sortMode = sortMode,
                location = location,
                displayedCameras = it.getDisplayedCameras(sortMode = sortMode, location = location)
            )
        }
    }

    fun changeFilterMode(filterMode: FilterMode) {
        val mode = if (filterMode == _cameraState.value.filterMode) {
            FilterMode.VISIBLE
        }
        else {
            filterMode
        }
        _cameraState.update {
            it.copy(
                displayedCameras = it.getDisplayedCameras(filterMode = mode),
                filterMode = mode,
            )
        }
    }

    fun favouriteCameras(cameras: List<Camera>) {
        val allFavourite = cameras.all { it.isFavourite }
        for (camera in _cameraState.value.allCameras) {
            if (camera in cameras) {
                camera.isFavourite = !allFavourite
                prefs.edit { putBoolean("${camera.id}.isFavourite", !allFavourite) }
            }
        }
        _cameraState.update { it.copy(lastUpdated = System.currentTimeMillis()) }
    }

    fun hideCameras(cameras: List<Camera>) {
        val anyVisible = cameras.any { it.isVisible }
        for (camera in _cameraState.value.allCameras) {
            if (camera in cameras) {
                camera.isVisible = !anyVisible
                prefs.edit { putBoolean("${camera.id}.isVisible", !anyVisible) }
            }
        }
        selectAllCameras(false)
        _cameraState.update {
            it.copy(
                lastUpdated = System.currentTimeMillis(),
                displayedCameras = it.getDisplayedCameras(),
            )
        }
    }

    fun selectCamera(camera: Camera) {
        for (cam in _cameraState.value.allCameras) {
            if (cam == camera) {
                cam.isSelected = !cam.isSelected
                break
            }
        }
        _cameraState.update { it.copy(lastUpdated = System.currentTimeMillis()) }
    }

    fun selectAllCameras(select: Boolean = true) {
        for (camera in _cameraState.value.allCameras) {
            if (camera in _cameraState.value.displayedCameras) {
                camera.isSelected = select
            }
        }
        _cameraState.update { it.copy(lastUpdated = System.currentTimeMillis()) }
    }

    fun searchCameras(searchMode: SearchMode = SearchMode.NONE, searchText: String = "") {
        _cameraState.update {
            it.copy(
                displayedCameras = it.getDisplayedCameras(searchMode = searchMode, searchText = searchText),
                searchMode = searchMode,
                searchText = searchText,
            )
        }
    }

    fun resetFilters() {
        _cameraState.update {
            it.copy(
                displayedCameras = it.getDisplayedCameras(
                    searchMode = SearchMode.NONE,
                    filterMode = FilterMode.VISIBLE,
                ),
                searchMode = SearchMode.NONE,
                filterMode = FilterMode.VISIBLE,
            )
        }
    }

    fun showCameras(
        context: Context,
        cameras: List<Camera> = ArrayList(),
        displayedCameras: List<Camera> = ArrayList(),
        shuffle: Boolean = false,
    ) {
        context.startActivity(
            Intent(context, CameraActivity::class.java).apply {
                putParcelableArrayListExtra("cameras", ArrayList(cameras))
                putParcelableArrayListExtra("displayedCameras", ArrayList(displayedCameras))
                putExtra("shuffle", shuffle)
            }
        )
    }

    fun download(context: Context) {
        // show the loading view
        _cameraState.update { it.copy(uiState = UIState.LOADING) }

        if (_cameraState.value.allCameras.isEmpty()) {
            downloadService.download(
                context,
                onComplete = { cameras ->
                    if (cameras.isEmpty()) {
                        // show an error if the retrieved camera list is empty
                        _cameraState.update { it.copy(uiState = UIState.ERROR) }
                    }
                    else {
                        val viewMode = ViewMode.valueOf(
                            prefs.getString("viewMode", ViewMode.LIST.name) ?: ViewMode.LIST.name
                        )
                        for (camera in cameras) {
                            camera.isFavourite = prefs.getBoolean("${camera.id}.isFavourite", false)
                            camera.isVisible = prefs.getBoolean("${camera.id}.isVisible", true)
                        }
                        _cameraState.update { cameraState ->
                            cameraState.copy(
                                allCameras = cameras,
                                displayedCameras = cameras.filter { it.isVisible },
                                uiState = UIState.LOADED,
                                viewMode = viewMode,
                            )
                        }
                    }
                },
                onError = {

                },
            )
        }
        else {
            // don't reload if the camera list is not empty
            _cameraState.update { it.copy(uiState = UIState.LOADED) }
        }
    }

    companion object {
        val MainViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                MainViewModel(
                    prefs = application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE)
                )
            }
        }
    }
}