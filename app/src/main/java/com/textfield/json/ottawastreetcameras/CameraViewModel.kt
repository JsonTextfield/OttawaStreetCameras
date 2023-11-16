package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CameraViewModel(
    private var _cameraState: MutableStateFlow<CameraState> = MutableStateFlow(CameraState()),
    private val downloadService: DownloadService = CameraDownloadService,
    private val prefs: SharedPreferences,
) : ViewModel() {

    val cameraState: StateFlow<CameraState>
        get() = _cameraState

    fun changeViewMode(viewMode: ViewMode) {
        prefs.edit().putString("viewMode", viewMode.name).apply()
        _cameraState.update { it.copy(viewMode = viewMode) }
    }

    fun changeSortMode(sortMode: SortMode, location: Location? = null) {
        when (sortMode) {
            SortMode.NAME -> _cameraState.value.displayedCameras.sortWith(SortByName())
            SortMode.NEIGHBOURHOOD -> _cameraState.value.displayedCameras.sortWith(SortByNeighbourhood())
            SortMode.DISTANCE -> _cameraState.value.displayedCameras.sortWith(SortByDistance(location!!))
        }
        _cameraState.update { it.copy(sortMode = sortMode) }
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
                displayedCameras = ArrayList(it.getSearchResults(it.searchMode, mode, it.searchText)),
                filterMode = mode,
            )
        }
    }

    fun favouriteCameras(cameras: List<Camera>) {
        val allFavourite = cameras.all { it.isFavourite }
        for (camera in _cameraState.value.allCameras) {
            if (camera in cameras) {
                camera.isFavourite = !allFavourite
                prefs.edit().putBoolean("${camera.num}.isFavourite", !allFavourite).apply()
            }
        }
        _cameraState.update { it.copy(lastUpdated = System.currentTimeMillis()) }
    }

    fun hideCameras(cameras: List<Camera>) {
        val anyVisible = cameras.any { it.isVisible }
        for (camera in _cameraState.value.allCameras) {
            if (camera in cameras) {
                camera.isVisible = !anyVisible
                prefs.edit().putBoolean("${camera.num}.isVisible", !anyVisible).apply()
            }
        }
        _cameraState.update {
            it.copy(
                lastUpdated = System.currentTimeMillis(),
                displayedCameras = ArrayList(
                    _cameraState.value.getSearchResults(
                        it.searchMode,
                        it.filterMode,
                        it.searchText,
                    )
                )
            )
        }
        selectAllCameras(false)
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
                displayedCameras = ArrayList(
                    _cameraState.value.getSearchResults(
                        searchMode,
                        _cameraState.value.filterMode,
                        searchText,
                    )
                ),
                searchMode = searchMode,
                searchText = searchText,
            )
        }
    }

    fun resetFilters() {
        _cameraState.update {
            it.copy(
                displayedCameras = ArrayList(it.visibleCameras),
                searchMode = SearchMode.NONE,
                filterMode = FilterMode.VISIBLE,
            )
        }
    }

    fun downloadAll(context: Context) {
        // show the loading view
        _cameraState.update { it.copy(uiState = UIState.LOADING) }

        if (_cameraState.value.allCameras.isEmpty()) {
            downloadService.downloadAll(context) { cameras, neighbourhoods ->
                if (cameras.isEmpty()) {
                    // show an error if the retrieved camera list is empty
                    _cameraState.update { it.copy(uiState = UIState.ERROR) }
                }
                else {
                    val viewMode = ViewMode.valueOf(
                        prefs.getString("viewMode", ViewMode.LIST.name) ?: ViewMode.LIST.name
                    )

                    for (camera in cameras) {
                        camera.isFavourite = prefs.getBoolean("${camera.num}.isFavourite", false)
                        camera.isVisible = prefs.getBoolean("${camera.num}.isVisible", true)
                        for (neighbourhood in neighbourhoods) {
                            if (neighbourhood.containsCamera(camera)) {
                                camera.neighbourhood = neighbourhood.name
                            }
                        }
                    }
                    // show the newly loaded station
                    _cameraState.update { cameraState ->
                        cameraState.copy(
                            allCameras = ArrayList(cameras),
                            displayedCameras = ArrayList(cameras.filter { it.isVisible }),
                            uiState = UIState.LOADED,
                            viewMode = viewMode,
                        )
                    }
                }
            }
        }
        else {
            // don't reload if the camera list is not empty
            _cameraState.update { it.copy(uiState = UIState.LOADED) }
        }
    }

    companion object {
        val CameraViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                CameraViewModel(
                    prefs = application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE)
                )
            }
        }
    }
}