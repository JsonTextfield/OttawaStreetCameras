package com.textfield.json.ottawastreetcameras.ui.main

import android.location.Location
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortByDistance
import com.textfield.json.ottawastreetcameras.SortByName
import com.textfield.json.ottawastreetcameras.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlin.math.roundToInt

enum class SortMode(val key: Int) {
    NAME(R.string.sort_by_name),
    DISTANCE(R.string.sort_by_distance),
    NEIGHBOURHOOD(R.string.sort_by_neighbourhood),
}

enum class FilterMode(val key: Int) {
    VISIBLE(R.string.app_name),
    FAVOURITE(R.string.favourites),
    HIDDEN(R.string.hidden_cameras),
}

enum class SearchMode { NONE, NAME, NEIGHBOURHOOD, }

enum class ViewMode(val key: Int) {
    LIST(R.string.list),
    MAP(R.string.map),
    GALLERY(R.string.gallery),
}

enum class UIState { LOADING, LOADED, ERROR, INITIAL, }

data class CameraState(
    val allCameras: List<Camera> = ArrayList(),
    val displayedCameras: List<Camera> = ArrayList(),
    val uiState: UIState = UIState.INITIAL,
    val sortMode: SortMode = SortMode.NAME,
    val searchMode: SearchMode = SearchMode.NONE,
    val searchText: String = "",
    val filterMode: FilterMode = FilterMode.VISIBLE,
    val viewMode: ViewMode = ViewMode.GALLERY,
    val location: Location? = null,
    val lastUpdated: Long = 0L,
) {
    val selectedCameras
        get() = allCameras.filter { it.isSelected }

    val visibleCameras
        get() = allCameras.filter { it.isVisible }

    val hiddenCameras
        get() = allCameras.filter { !it.isVisible }

    val favouriteCameras
        get() = allCameras.filter { it.isFavourite }

    val showSectionIndex
        get() = filterMode == FilterMode.VISIBLE
                && sortMode == SortMode.NAME
                && searchMode == SearchMode.NONE
                && viewMode == ViewMode.LIST

    val showSearchNeighbourhood
        get() = uiState == UIState.LOADED && searchMode != SearchMode.NEIGHBOURHOOD

    val showBackButton
        get() = (filterMode != FilterMode.VISIBLE || searchMode != SearchMode.NONE) &&
                selectedCameras.isEmpty()

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
        searchText: String
    ): (camera: Camera) -> Boolean {
        return when (searchMode) {
            SearchMode.NONE -> { _ -> true }
            SearchMode.NAME -> { camera -> camera.name.contains(searchText.trim(), true) }
            SearchMode.NEIGHBOURHOOD -> { camera ->
                camera.neighbourhood.contains(
                    searchText.trim(),
                    true
                )
            }
        }
    }

    private fun getCameraComparator(
        sortMode: SortMode,
        location: Location? = null
    ): Comparator<Camera> {
        return when (sortMode) {
            SortMode.NAME -> SortByName
            SortMode.NEIGHBOURHOOD -> SortByNeighbourhood
            SortMode.DISTANCE -> {
                if (location != null) {
                    for (camera in allCameras) {
                        val result = FloatArray(3)
                        Location.distanceBetween(
                            location.latitude,
                            location.longitude,
                            camera.lat,
                            camera.lon,
                            result
                        )
                        camera.distance = result[0].roundToInt()
                    }
                    SortByDistance
                }
                else {
                    SortByName
                }
            }
        }
    }

    fun getDisplayedCameras(
        searchMode: SearchMode = this.searchMode,
        filterMode: FilterMode = this.filterMode,
        searchText: String = this.searchText,
        sortMode: SortMode = this.sortMode,
        location: Location? = this.location,
    ): List<Camera> {
        return filterCameras(filterMode)
            .filter(getSearchPredicate(searchMode, searchText))
            .sortedWith(getCameraComparator(sortMode, location))
    }

}