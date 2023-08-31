package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood


enum class SortMode { NAME, DISTANCE, NEIGHBOURHOOD, }

enum class FilterMode { VISIBLE, FAVOURITE, HIDDEN, }

enum class SearchMode { NONE, NAME, NEIGHBOURHOOD, }

enum class ViewMode { LIST, MAP, GALLERY }

enum class UIState { LOADING, LOADED, ERROR, INITIAL }

data class CameraState(
    var allCameras: ArrayList<Camera> = ArrayList(),
    var displayedCameras: ArrayList<Camera> = ArrayList(),
    var selectedCameras: List<Camera> = ArrayList(),
    var neighbourhoods: List<Neighbourhood> = ArrayList(),
    var uiState: UIState = UIState.INITIAL,
    var sortMode: SortMode = SortMode.NAME,
    var searchMode: SearchMode = SearchMode.NONE,
    var filterMode: FilterMode = FilterMode.VISIBLE,
    var viewMode: ViewMode = ViewMode.LIST,
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

    fun filterCameras() : List<Camera>{
        return when (filterMode) {
            FilterMode.VISIBLE -> visibleCameras
            FilterMode.HIDDEN -> hiddenCameras
            FilterMode.FAVOURITE -> favouriteCameras
        }
    }
}