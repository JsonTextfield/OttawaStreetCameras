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

class CameraManager : ViewModel() {
    private var _cameraState = MutableStateFlow(
        CameraState(
            allCameras = ArrayList(),
            displayedCameras = ArrayList(),
            selectedCameras = ArrayList(),
            neighbourhoods = ArrayList(),
            uiState = UIState.INITIAL,
            sortMode = SortMode.NAME,
            searchMode = SearchMode.NONE,
            filterMode = FilterMode.VISIBLE,
            viewMode = ViewMode.LIST,
            lastUpdated = 0
        )
    )

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

            SortMode.NEIGHBOURHOOD -> displayedCameras.sortWith(SortByNeighbourhood())
        }
        _cameraState.update {
            it.copy(
                sortMode = sortMode,
                displayedCameras = displayedCameras,
            )
        }
    }

    fun changeSearchMode(searchMode: SearchMode) {
        _cameraState.update {
            it.copy(searchMode = if (searchMode == _cameraState.value.searchMode) SearchMode.NONE else searchMode)
        }
    }

    fun changeFilterMode(filterMode: FilterMode) {
        val fm = if (filterMode == _cameraState.value.filterMode) FilterMode.VISIBLE else filterMode
        val displayedCameras =
            when (fm) {
                FilterMode.VISIBLE -> _cameraState.value.visibleCameras
                FilterMode.HIDDEN -> _cameraState.value.hiddenCameras
                FilterMode.FAVOURITE -> _cameraState.value.favouriteCameras
            }
        _cameraState.update {
            it.copy(
                displayedCameras = displayedCameras as ArrayList<Camera>,
                filterMode = fm,
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
                    cam.isFavourite = !cam.isFavourite
                    break
                }
            }

            val updatedDisplayedCameras = ArrayList<Camera>(it.displayedCameras)
            it.displayedCameras.clear()
            for (cam in updatedDisplayedCameras) {
                if (cam == camera) {
                    cam.isFavourite = !cam.isFavourite
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

    fun favouriteSelectedCameras(context: Context) {
        for (camera in _cameraState.value.selectedCameras) {
            camera.isFavourite = !camera.isFavourite
            favouriteCamera(context, camera)
        }
    }

    fun hideSelectedCameras(context: Context) {
        for (camera in _cameraState.value.selectedCameras) {
            camera.isVisible = !camera.isVisible
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

    fun searchCameras(query: String = "") {
        val updatedCameras = when (_cameraState.value.searchMode) {
            SearchMode.NONE -> {
                _cameraState.value.allCameras.filter { it.isVisible }
            }

            SearchMode.NAME -> {
                _cameraState.value.allCameras.filter {
                    it.isVisible && it.name.contains(query.trim(), true)
                }
            }

            SearchMode.NEIGHBOURHOOD -> {
                _cameraState.value.allCameras.filter {
                    it.isVisible && it.neighbourhood.contains(query.trim(), true)
                }
            }
        } as ArrayList<Camera>
        _cameraState.update { it.copy(displayedCameras = updatedCameras) }
    }

    fun downloadAll(context: Context) {
        // show the loading view
        _cameraState.update { it.copy(uiState = UIState.LOADING) }

        if (_cameraState.value.allCameras.isEmpty() || _cameraState.value.neighbourhoods.isEmpty()) {
            DownloadService.downloadAll(context) { cameras, neighbourhoods ->
                if (cameras.isEmpty()) {
                    // show an error if the retrieved camera list is empty
                    _cameraState.update { it.copy(uiState = UIState.ERROR) }
                }
                else {
                    val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
                    val viewMode =
                        ViewMode.valueOf(sharedPrefs.getString("viewMode", ViewMode.LIST.name) ?: ViewMode.LIST.name)

                    for (camera in cameras) {
                        camera.isFavourite = sharedPrefs.getBoolean("${camera.num}.isFavourite", false)
                        camera.isVisible = sharedPrefs.getBoolean("${camera.num}.isVisible", true)
                        for (neighbourhood in neighbourhoods) {
                            if (neighbourhood.containsCamera(camera)) {
                                camera.neighbourhood = neighbourhood.name
                            }
                        }
                    }
                    val displayedCameras = cameras.filter {
                        when (_cameraState.value.filterMode) {
                            FilterMode.VISIBLE -> it.isVisible
                            FilterMode.HIDDEN -> !it.isVisible
                            FilterMode.FAVOURITE -> it.isFavourite
                        }
                    }
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

    companion object {
        @Volatile
        private var INSTANCE: CameraManager? = null
        fun getInstance(): CameraManager = INSTANCE ?: synchronized(this) {
            INSTANCE ?: CameraManager().also {
                INSTANCE = it
            }
        }
    }
}