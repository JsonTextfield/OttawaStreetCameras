package com.textfield.json.ottawastreetcameras

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.activities.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private var _cameraState: MutableStateFlow<CameraState> = MutableStateFlow(CameraState()),
    private val downloadService: DownloadService = CameraDownloadService,
) : ViewModel() {

    val cameraState: StateFlow<CameraState>
        get() = _cameraState

    fun changeViewMode(context: Context, viewMode: ViewMode) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("viewMode", viewMode.name).apply()
        _cameraState.update {
            it.copy(viewMode = viewMode)
        }
    }

    fun changeSortMode(context: Context, sortMode: SortMode) {
        val displayedCameras = ArrayList<Camera>(_cameraState.value.displayedCameras)
        when (sortMode) {
            SortMode.NAME -> displayedCameras.sortWith(SortByName())
            SortMode.NEIGHBOURHOOD -> displayedCameras.sortWith(SortByNeighbourhood())
            SortMode.DISTANCE -> {
                if (context is MainActivity) {
                    context.requestLocationPermissions(0) { location ->
                        viewModelScope.launch {
                            displayedCameras.sortWith(SortByDistance(location))
                            _cameraState.update {
                                it.copy(
                                    sortMode = sortMode,
                                    displayedCameras = displayedCameras,
                                )
                            }
                        }
                    }
                }
            }
        }
        _cameraState.update {
            it.copy(
                sortMode = sortMode,
                displayedCameras = displayedCameras,
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
        val displayedCameras = when (mode) {
            FilterMode.VISIBLE -> _cameraState.value.visibleCameras
            FilterMode.HIDDEN -> _cameraState.value.hiddenCameras
            FilterMode.FAVOURITE -> _cameraState.value.favouriteCameras
        } as ArrayList<Camera>
        _cameraState.update {
            it.copy(
                displayedCameras = displayedCameras,
                filterMode = mode,
            )
        }
    }

    fun favouriteCamera(context: Context, camera: Camera) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("${camera.num}.isFavourite", camera.isFavourite).apply()

        _cameraState.update {
            val updatedCameras = ArrayList<Camera>(it.allCameras)
            it.allCameras.clear()
            for (cam in updatedCameras) {
                if (cam == camera) {
                    cam.isFavourite = camera.isFavourite
                    break
                }
            }

            val updatedDisplayedCameras = ArrayList<Camera>(it.displayedCameras)
            it.displayedCameras.clear()
            for (cam in updatedDisplayedCameras) {
                if (cam == camera) {
                    cam.isFavourite = camera.isFavourite
                    break
                }
            }

            it.copy(
                allCameras = updatedCameras,
                displayedCameras = updatedDisplayedCameras
            )
        }
    }

    fun hideCamera(context: Context, camera: Camera) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("${camera.num}.isVisible", camera.isVisible).apply()

        _cameraState.update {
            val updatedCameras = ArrayList<Camera>(it.allCameras)
            it.allCameras.clear()
            for (cam in updatedCameras) {
                if (cam == camera) {
                    cam.isVisible = camera.isVisible
                    break
                }
            }

            val updatedDisplayedCameras = ArrayList<Camera>(it.displayedCameras)
            updatedDisplayedCameras.remove(camera)

            it.copy(
                allCameras = updatedCameras,
                displayedCameras = updatedDisplayedCameras,
            )
        }
    }

    fun favouriteSelectedCameras(context: Context, isFavourite: Boolean) {
        for (camera in _cameraState.value.selectedCameras) {
            camera.isFavourite = isFavourite
            favouriteCamera(context, camera)
        }
    }

    fun hideSelectedCameras(context: Context, isVisible: Boolean) {
        for (camera in _cameraState.value.selectedCameras) {
            camera.isVisible = isVisible
            hideCamera(context, camera)
        }
        clearSelectedCameras()
    }

    fun selectCamera(camera: Camera) {
        val selectedCameras = ArrayList<Camera>(_cameraState.value.selectedCameras)
        if (selectedCameras.contains(camera)) {
            selectedCameras.remove(camera)
        }
        else {
            selectedCameras.add(camera)
        }
        _cameraState.update { it.copy(selectedCameras = selectedCameras) }
    }

    fun selectAllCameras() {
        _cameraState.update { it.copy(selectedCameras = it.displayedCameras) }
    }

    fun clearSelectedCameras() {
        _cameraState.update { it.copy(selectedCameras = ArrayList()) }
    }

    fun changeSearchMode(searchMode: SearchMode) {
        _cameraState.update {
            it.copy(
                searchMode = if (searchMode == _cameraState.value.searchMode) {
                    SearchMode.NONE
                }
                else {
                    searchMode
                }
            )
        }
    }

    fun searchCameras(str: String = "") {
        val updatedCameras = _cameraState.value.filterCameras().filter {
            when (_cameraState.value.searchMode) {
                SearchMode.NAME -> it.name.contains(str.trim(), true)
                SearchMode.NEIGHBOURHOOD -> it.neighbourhood.contains(str.trim(), true)
                SearchMode.NONE -> true
            }
        } as ArrayList<Camera>
        _cameraState.update { it.copy(displayedCameras = updatedCameras) }
    }

    fun downloadAll(context: Context) {
        // show the loading view
        _cameraState.update { it.copy(uiState = UIState.LOADING) }

        if (_cameraState.value.allCameras.isEmpty() || _cameraState.value.neighbourhoods.isEmpty()) {
            downloadService.downloadAll(context) { cameras, neighbourhoods ->
                if (cameras.isEmpty()) {
                    // show an error if the retrieved camera list is empty
                    _cameraState.update { it.copy(uiState = UIState.ERROR) }
                }
                else {
                    val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
                    val viewMode = ViewMode.valueOf(
                        sharedPrefs.getString("viewMode", ViewMode.LIST.name) ?: ViewMode.LIST.name
                    )

                    for (camera in cameras) {
                        camera.isFavourite = sharedPrefs.getBoolean("${camera.num}.isFavourite", false)
                        camera.isVisible = sharedPrefs.getBoolean("${camera.num}.isVisible", true)
                        for (neighbourhood in neighbourhoods) {
                            if (neighbourhood.containsCamera(camera)) {
                                camera.neighbourhood = neighbourhood.name
                            }
                        }
                    }
                    val displayedCameras = cameras.filter { it.isVisible }
                    // show the newly loaded station
                    _cameraState.update {
                        it.copy(
                            allCameras = ArrayList(cameras),
                            displayedCameras = ArrayList(displayedCameras),
                            neighbourhoods = neighbourhoods,
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
}