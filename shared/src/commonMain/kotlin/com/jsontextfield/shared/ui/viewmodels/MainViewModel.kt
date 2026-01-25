package com.jsontextfield.shared.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.shared.data.ICameraRepository
import com.jsontextfield.shared.data.IPreferencesRepository
import com.jsontextfield.shared.entities.Camera
import com.jsontextfield.shared.entities.City
import com.jsontextfield.shared.ui.FilterMode
import com.jsontextfield.shared.ui.SearchMode
import com.jsontextfield.shared.ui.SortMode
import com.jsontextfield.shared.ui.Status
import com.jsontextfield.shared.ui.ThemeMode
import com.jsontextfield.shared.ui.ViewMode
import com.jsontextfield.shared.ui.main.CameraState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        prefs
            .getTheme()
            .map {
                _theme.value = it
            }.launchIn(viewModelScope)
        prefs
            .getCity()
            .distinctUntilChanged()
            .map { city ->
                _uiState.update {
                    it.copy(
                        status = Status.LOADING,
                        city = city,
                        allCameras = emptyList(),
                        sortMode = SortMode.NAME,
                        searchMode = SearchMode.NONE,
                        filterMode = FilterMode.VISIBLE,
                    )
                }
                //delay(2000)
                loadData(city)
            }.launchIn(viewModelScope)
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
        location: Pair<Double, Double>? = null,
    ) {
        _uiState.update {
            val allCameras = it.allCameras.map { camera ->
                camera.copy(
                    distance = if (location != null) {
                        getDistanceBetween(
                            location.first,
                            location.second,
                            camera.lat,
                            camera.lon
                        )
                    } else {
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
        } else {
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
            val ids = cameras.map { it.id }.toSet()
            val favouriteCameras = prefs.getFavourites().first()
            val newFavourites = if (favouriteCameras.containsAll(ids)) {
                favouriteCameras - ids
            } else {
                favouriteCameras + ids
            }.toSet()
            prefs.setFavouriteCameras(newFavourites)
        }
    }

    fun hideCameras(cameras: List<Camera>) {
        viewModelScope.launch {
            val hiddenCameras = uiState.value.hiddenCameras
            val newHidden = if (hiddenCameras.containsAll(cameras)) {
                hiddenCameras - cameras
            } else {
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
                    } else {
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

    fun reloadData() {
        _uiState.update { it.copy(status = Status.LOADING) }
        loadData(_uiState.value.city)
    }

    fun loadData(city: City) {
        viewModelScope.launch {
            runCatching {
                cameraRepository.getAllCameras(city)
            }.onSuccess { cameras ->
                combine(
                    prefs.getHidden(),
                    prefs.getFavourites(),
                    prefs.getViewMode(),
                ) { hidden, favourites, viewMode ->
                    _uiState.update { cameraState ->
                        cameraState.copy(
                            status = Status.LOADED,
                            allCameras = cameras.map {
                                it.copy(
                                    isVisible = it.id !in hidden,
                                    isFavourite = it.id in favourites,
                                )
                            },
                            viewMode = viewMode,
                        )
                    }
                }.collect()
            }.onFailure {
                _uiState.update { it.copy(status = Status.ERROR) }
            }
        }
    }

    fun changeCity(city: City) {
        viewModelScope.launch {
            prefs.setCity(city)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        searchJob = null
    }
}

expect fun getDistanceBetween(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double,
) : Int