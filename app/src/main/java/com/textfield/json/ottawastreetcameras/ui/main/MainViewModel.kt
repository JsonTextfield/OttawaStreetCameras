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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainViewModel(
    private val _uiState: MutableStateFlow<CameraState>,
    private val cameraRepository: ICameraRepository,
    private val prefs: IPreferencesRepository,
) : ViewModel() {
    private var searchJob: Job? = null
    val uiState: StateFlow<CameraState> get() = _uiState.asStateFlow()
    var searchText by mutableStateOf("")
        private set

    val suggestionList: List<String>
        get() {
            if (uiState.value.searchMode != SearchMode.NEIGHBOURHOOD || searchText.isEmpty()) {
                return emptyList()
            }
            val filteredNeighbourhoods = uiState.value.neighbourhoods.filter {
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
        viewModelScope.launch {
            _theme.value = prefs.getTheme() ?: ThemeMode.SYSTEM
            getAllCameras()
        }
    }

    fun changeTheme(theme: ThemeMode) {
        viewModelScope.launch {
            prefs.setTheme(theme)
            _theme.value = theme
        }
    }

    fun changeViewMode(viewMode: ViewMode) {
        viewModelScope.launch {
            prefs.setViewMode(viewMode)
            _uiState.update { it.copy(viewMode = viewMode) }
        }
    }

    fun changeSortMode(
        sortMode: SortMode,
        location: Location? = null,
    ) {
        _uiState.update {
            val allCameras = it.allCameras.map { camera ->
                camera.copy(
                    distance = if (location != null) {
                        val result = FloatArray(1)
                        Location.distanceBetween(
                            location.latitude,
                            location.longitude,
                            camera.lat,
                            camera.lon,
                            result
                        )
                        result[0].roundToInt()
                    }
                    else {
                        -1
                    }
                )
            }
            it.copy(
                allCameras = allCameras,
                sortMode = sortMode,
            )
        }
    }

    fun changeFilterMode(filterMode: FilterMode) {
        val mode = if (filterMode == _uiState.value.filterMode) {
            FilterMode.VISIBLE
        }
        else {
            filterMode
        }
        _uiState.update {
            it.copy(
                filterMode = mode,
            )
        }
    }

    fun favouriteCameras(cameras: List<Camera>) {
        viewModelScope.launch {
            val allFavourite = cameras.all { it.isFavourite }
            prefs.favourite(cameras.map { it.id }, !allFavourite)
            val favourites = prefs.getFavourites()
            _uiState.update {
                it.copy(
                    allCameras = it.allCameras.map { camera ->
                        camera.copy(isFavourite = camera.id in favourites)
                    }
                )
            }
        }
    }

    fun hideCameras(cameras: List<Camera>) {
        viewModelScope.launch {
            var hidden = prefs.getHidden()
            prefs.setVisibility(
                cameras.map { it.id },
                cameras.first().id in hidden,
            )
            hidden = prefs.getHidden()
            _uiState.update {
                it.copy(
                    allCameras = it.allCameras.map { camera ->
                        camera.copy(isVisible = camera.id !in hidden)
                    }
                )
            }
        }
    }

    fun selectCamera(camera: Camera) {
        _uiState.update { cameraState ->
            cameraState.copy(
                allCameras = cameraState.allCameras.map { cam ->
                    if (cam == camera) cam.copy(isSelected = !cam.isSelected) else cam
                }
            )
        }
    }

    fun selectAllCameras(select: Boolean = true) {
        _uiState.update { cameraState ->
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

    fun searchCameras(
        searchMode: SearchMode = SearchMode.NONE,
        searchText: String = "",
    ) {
        this.searchText = searchText
        _uiState.update { it.copy(searchMode = searchMode) }
    }

    fun resetFilters() {
        _uiState.update {
            it.copy(
                searchMode = SearchMode.NONE,
                filterMode = FilterMode.VISIBLE,
            )
        }
    }

    fun getAllCameras() {
        _uiState.update { it.copy(status = Status.LOADING) }
        viewModelScope.launch {
            val hidden = prefs.getHidden()
            val favourites = prefs.getFavourites()
            runCatching {
                cameraRepository.getAllCameras()
            }.onSuccess { cameras ->
                val viewMode = prefs.getViewMode() ?: ViewMode.GALLERY
                _uiState.update { cameraState ->
                    cameraState.copy(
                        allCameras = cameras.map {
                            it.copy(
                                isVisible = it.id !in hidden,
                                isFavourite = it.id in favourites,
                            )
                        },
                        status = Status.LOADED,
                        viewMode = viewMode,
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(status = Status.ERROR) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        searchJob = null
    }
}