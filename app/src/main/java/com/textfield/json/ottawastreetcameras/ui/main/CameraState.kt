package com.textfield.json.ottawastreetcameras.ui.main

import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

enum class ThemeMode(val key: Int) {
    LIGHT(R.string.light_mode),
    DARK(R.string.dark_mode),
    SYSTEM(R.string.system_default),
}

enum class SortMode(val key: Int) {
    NAME(R.string.sort_by_name),
    DISTANCE(R.string.sort_by_distance),
    NEIGHBOURHOOD(R.string.sort_by_neighbourhood),
}

enum class FilterMode(val key: Int) {
    VISIBLE(R.string.all),
    FAVOURITE(R.string.favourites),
    HIDDEN(R.string.hidden_cameras),
}

enum class SearchMode { NONE, NAME, NEIGHBOURHOOD, }

enum class ViewMode(val key: Int) {
    LIST(R.string.list),
    MAP(R.string.map),
    GALLERY(R.string.gallery),
}

enum class Status { LOADING, LOADED, ERROR, INITIAL, }

data class CameraState(
    val allCameras: List<Camera> = ArrayList(),
    val status: Status = Status.INITIAL,
    val sortMode: SortMode = SortMode.NAME,
    val searchMode: SearchMode = SearchMode.NONE,
    val filterMode: FilterMode = FilterMode.VISIBLE,
    val viewMode: ViewMode = ViewMode.GALLERY,
) {
    val selectedCameras: List<Camera>
        get() = allCameras.filter { it.isSelected }

    val visibleCameras: List<Camera>
        get() = allCameras.filter { it.isVisible }

    val hiddenCameras: List<Camera>
        get() = allCameras.filter { !it.isVisible }

    val favouriteCameras: List<Camera>
        get() = allCameras.filter { it.isFavourite }

    val showSectionIndex
        get() = filterMode == FilterMode.VISIBLE
                && sortMode == SortMode.NAME
                && searchMode == SearchMode.NONE
                && viewMode == ViewMode.LIST

    val showSearchNeighbourhood
        get() = status == Status.LOADED && searchMode != SearchMode.NEIGHBOURHOOD

    val showBackButton
        get() = searchMode != SearchMode.NONE && selectedCameras.isEmpty()

    val neighbourhoods
        get() = allCameras.map { it.neighbourhood }.distinct()

    private fun filterCameras(filterMode: FilterMode): List<Camera> {
        return when (filterMode) {
            FilterMode.VISIBLE -> visibleCameras
            FilterMode.HIDDEN -> hiddenCameras
            FilterMode.FAVOURITE -> favouriteCameras
        }
    }

    private fun getSearchPredicate(
        searchMode: SearchMode,
        searchText: String,
    ): (camera: Camera) -> Boolean {
        return when (searchMode) {
            SearchMode.NONE -> { _ -> true }
            SearchMode.NAME -> { camera ->
                camera.name.contains(
                    searchText.trim(),
                    true,
                )
            }

            SearchMode.NEIGHBOURHOOD -> { camera ->
                camera.neighbourhood.contains(
                    searchText.trim(),
                    true,
                )
            }
        }
    }

    private fun getCameraComparator(sortMode: SortMode): Comparator<Camera> {
        return when (sortMode) {
            SortMode.NAME -> compareBy { it.sortableName }
            SortMode.NEIGHBOURHOOD -> compareBy({ it.neighbourhood }, { it.sortableName })
            SortMode.DISTANCE -> compareBy({ it.distance }, { it.sortableName })
        }
    }

    fun getDisplayedCameras(searchText: String = ""): List<Camera> {
        return filterCameras(filterMode)
            .filter(getSearchPredicate(searchMode, searchText))
            .sortedWith(getCameraComparator(sortMode))
    }
}