package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera

enum class SortMode(val key: Int) {
    NAME(R.string.sort_by_name),
    DISTANCE(R.string.sort_by_distance),
    NEIGHBOURHOOD(R.string.sort_by_neighbourhood),
}

enum class FilterMode { VISIBLE, FAVOURITE, HIDDEN, }

enum class SearchMode { NONE, NAME, NEIGHBOURHOOD, }

enum class ViewMode(val key: Int) {
    LIST(R.string.list),
    MAP(R.string.map),
    GALLERY(R.string.gallery),
}

enum class UIState { LOADING, LOADED, ERROR, INITIAL, }

data class CameraState(
    var allCameras: ArrayList<Camera> = ArrayList(),
    var displayedCameras: ArrayList<Camera> = ArrayList(),
    var selectedCameras: List<Camera> = ArrayList(),
    var neighbourhoods: List<String> = ArrayList(),
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
            filterMode == FilterMode.VISIBLE
            && sortMode == SortMode.NAME
            && searchMode == SearchMode.NONE
            && viewMode == ViewMode.LIST

    fun filterCameras(): List<Camera> {
        return when (filterMode) {
            FilterMode.VISIBLE -> visibleCameras
            FilterMode.HIDDEN -> hiddenCameras
            FilterMode.FAVOURITE -> favouriteCameras
        }
    }
}