package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood


enum class SortMode { NAME, DISTANCE, NEIGHBOURHOOD, }

enum class FilterMode { VISIBLE, FAVOURITE, HIDDEN, }

enum class SearchMode { NONE, NAME, NEIGHBOURHOOD, }

enum class ViewMode { LIST, MAP, GALLERY }

enum class UIState { LOADING, LOADED, ERROR, INITIAL }

data class CameraState(
    var allCameras: List<Camera>,
    var displayedCameras: List<Camera>,
    var selectedCameras: List<Camera>,
    var neighbourhoods: List<Neighbourhood>,
    var uiState: UIState,
    var sortMode: SortMode,
    var searchMode: SearchMode,
    var filterMode: FilterMode,
    var viewMode: ViewMode,
    var lastUpdated: Int,
) {
    val visibleCameras
        get() = allCameras.filter { it.isVisible }

    val hiddenCameras
        get() = allCameras.filter { !it.isVisible }

    val favouriteCameras
        get() = allCameras.filter { it.isFavourite }

    val showSectionIndex
        get() =
            filterMode == FilterMode.VISIBLE &&
            sortMode == SortMode.NAME &&
            searchMode == SearchMode.NONE &&
            viewMode == ViewMode.LIST

}