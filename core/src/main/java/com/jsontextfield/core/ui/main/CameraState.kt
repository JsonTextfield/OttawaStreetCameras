package com.jsontextfield.core.ui.main

import com.jsontextfield.core.entities.Camera
import com.jsontextfield.core.ui.FilterMode
import com.jsontextfield.core.ui.SearchMode
import com.jsontextfield.core.ui.SortMode
import com.jsontextfield.core.ui.Status
import com.jsontextfield.core.ui.ViewMode

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