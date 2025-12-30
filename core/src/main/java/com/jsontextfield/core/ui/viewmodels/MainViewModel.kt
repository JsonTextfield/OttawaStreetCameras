package com.jsontextfield.core.ui.viewmodels

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.core.data.ICameraRepository
import com.jsontextfield.core.data.IPreferencesRepository
import com.jsontextfield.core.entities.Camera
import com.jsontextfield.core.entities.City
import com.jsontextfield.core.ui.FilterMode
import com.jsontextfield.core.ui.SearchMode
import com.jsontextfield.core.ui.SortMode
import com.jsontextfield.core.ui.Status
import com.jsontextfield.core.ui.ThemeMode
import com.jsontextfield.core.ui.ViewMode
import com.jsontextfield.core.ui.main.CameraState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
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
            prefs.getTheme().collect {
                _theme.value = it
            }
        }
        getAllCameras()
    }

    fun changeTheme(theme: ThemeMode) {
        viewModelScope.launch {
            prefs.setTheme(theme)
        }
    }

    fun changeViewMode(viewMode: ViewMode) {
        viewModelScope.launch {
            prefs.setViewMode(viewMode)
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
        val favouriteCameras = uiState.value.favouriteCameras
        val newFavourites = if (favouriteCameras.containsAll(cameras)) {
            favouriteCameras - cameras
        }
        else {
            favouriteCameras + cameras
        }.map {
            it.id
        }.toSet()
        viewModelScope.launch {
            prefs.setFavouriteCameras(newFavourites)
        }
    }

    fun hideCameras(cameras: List<Camera>) {
        viewModelScope.launch {
            val hiddenCameras = uiState.value.hiddenCameras
            val newHidden = if (hiddenCameras.containsAll(cameras)) {
                hiddenCameras - cameras
            }
            else {
                hiddenCameras + cameras
            }.map {
                it.id
            }.toSet()
            viewModelScope.launch {
                prefs.setHiddenCameras(newHidden)
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
            combine(
                prefs.getHidden(),
                prefs.getFavourites(),
                prefs.getViewMode(),
            ) { hidden, favourites, viewMode ->
                runCatching {
                    cameraRepository.getAllCameras()
                }.onSuccess { cameras ->
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
            }.collect()
        }
    }

    fun changeCity(city: City) {
        _uiState.update {
            it.copy(
                currentCity = city
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        searchJob = null
    }
}