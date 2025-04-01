package com.textfield.json.ottawastreetcameras.ui.main

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.textfield.json.ottawastreetcameras.data.ICameraRepository
import com.textfield.json.ottawastreetcameras.data.IPreferencesRepository
import com.textfield.json.ottawastreetcameras.entities.Camera
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val _cameraState: MutableStateFlow<CameraState> = MutableStateFlow(CameraState()),
    private val cameraRepository: ICameraRepository,
    private val prefs: IPreferencesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private var searchJob: Job? = null
    val cameraState: StateFlow<CameraState> get() = _cameraState.asStateFlow()
    var searchText by mutableStateOf("")
        private set

    val suggestionList: List<String>
        get() {
            if (cameraState.value.searchMode != SearchMode.NEIGHBOURHOOD || searchText.isEmpty()) {
                return emptyList()
            }
            val filteredNeighbourhoods = cameraState.value.neighbourhoods.filter {
                it.contains(searchText, true)
            }
            if (filteredNeighbourhoods.all { it.equals(searchText, true) }) {
                return emptyList()
            }
            return filteredNeighbourhoods
        }

    private var _theme: MutableStateFlow<ThemeMode> = MutableStateFlow(ThemeMode.SYSTEM)
    val theme: StateFlow<ThemeMode> get() = _theme.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            _theme.value = prefs.getTheme()
            getAllCameras()
        }
    }

    fun changeTheme(theme: ThemeMode) {
        viewModelScope.launch(dispatcher) {
            prefs.setTheme(theme)
            _theme.value = theme
        }
    }

    fun changeViewMode(viewMode: ViewMode) {
        viewModelScope.launch(dispatcher) {
            prefs.setViewMode(viewMode)
            _cameraState.update { it.copy(viewMode = viewMode) }
        }
    }

    fun changeSortMode(sortMode: SortMode, location: Location? = null) {
        _cameraState.update {
            it.copy(
                sortMode = sortMode,
                location = location,
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
                filterMode = mode,
            )
        }
    }

    fun favouriteCameras(cameras: List<Camera>) {
        viewModelScope.launch(dispatcher) {
            val allFavourite = cameras.all { it.isFavourite }
            prefs.favourite(cameras.map { it.id }, !allFavourite)
            _cameraState.update { it.copy(allCameras = cameraRepository.getAllCameras()) }
        }
    }

    fun hideCameras(cameras: List<Camera>) {
        viewModelScope.launch(dispatcher) {
            val anyVisible = _cameraState.value.allCameras
                .filter { it in cameras }
                .any { it.isVisible }
            prefs.setVisibility(cameras.map { it.id }, !anyVisible)
            _cameraState.update {
                it.copy(allCameras = cameraRepository.getAllCameras())
            }
        }
    }

    fun selectCamera(camera: Camera) {
        _cameraState.update { cameraState ->
            cameraState.copy(
                allCameras = cameraState.allCameras.map { cam ->
                    if (cam == camera) cam.copy(isSelected = !cam.isSelected) else cam
                }
            )
        }
    }

    fun selectAllCameras(select: Boolean = true) {
        _cameraState.update { cameraState ->
            val displayedCameras = cameraState.getDisplayedCameras(searchText)
            cameraState.copy(
                allCameras = cameraState.allCameras.map { cam ->
                    if (cam in displayedCameras) {
                        cam.copy(isSelected = select)
                    }
                    else {
                        cam
                    }
                }
            )
        }
    }

    fun searchCameras(searchMode: SearchMode = SearchMode.NONE, searchText: String = "") {
        searchJob?.cancel()
        this.searchText = searchText
        searchJob = viewModelScope.launch(dispatcher) {
            delay(1000)
            _cameraState.update { it.copy(searchMode = searchMode) }
        }
    }

    fun resetFilters() {
        _cameraState.update {
            it.copy(
                searchMode = SearchMode.NONE,
                filterMode = FilterMode.VISIBLE,
            )
        }
    }

    fun getAllCameras() {
        _cameraState.update { it.copy(uiState = UIState.LOADING) }
        viewModelScope.launch(dispatcher) {
            val cameras = cameraRepository.getAllCameras()
            if (cameras.isEmpty()) {
                // show an error if the retrieved camera list is empty
                _cameraState.update { it.copy(uiState = UIState.ERROR) }
            }
            else {
                val viewMode = prefs.getViewMode()
                _cameraState.update { cameraState ->
                    cameraState.copy(
                        allCameras = cameras,
                        uiState = UIState.LOADED,
                        viewMode = viewMode,
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        searchJob = null
    }
}