package com.jsontextfield.shared

import com.jsontextfield.shared.entities.Camera
import com.jsontextfield.shared.network.model.CameraApiModel
import com.jsontextfield.shared.network.model.LocationApiModel
import com.jsontextfield.shared.ui.FilterMode
import com.jsontextfield.shared.ui.SearchMode
import com.jsontextfield.shared.ui.SortMode
import com.jsontextfield.shared.ui.Status
import com.jsontextfield.shared.ui.ViewMode
import com.jsontextfield.shared.ui.main.CameraState
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CameraStateUnitTest {
    private var cameraState = CameraState()

    @BeforeTest
    fun setup() {
        cameraState = CameraState()
    }

    @Test
    fun testInitialState() {
        assertEquals(cameraState, CameraState())
        assertEquals(cameraState.allCameras, listOf<Camera>())
        assertEquals(cameraState.status, Status.INITIAL)
        assertEquals(cameraState.sortMode, SortMode.NAME)
        assertEquals(cameraState.searchMode, SearchMode.NONE)
        assertEquals(cameraState.filterMode, FilterMode.VISIBLE)
        assertEquals(cameraState.viewMode, ViewMode.GALLERY)

        //read-only properties
        assertEquals(cameraState.selectedCameras, listOf<Camera>())
        assertEquals(cameraState.visibleCameras, listOf<Camera>())
        assertEquals(cameraState.hiddenCameras, listOf<Camera>())
        assertEquals(cameraState.favouriteCameras, listOf<Camera>())
        assertEquals(cameraState.showSectionIndex, false)
        assertEquals(cameraState.showSearchNeighbourhood, false)
        assertEquals(cameraState.showBackButton, false)
        assertEquals(cameraState.neighbourhoods, listOf<String>())
    }

    @Test
    fun testFavourites() {
        cameraState = cameraState.copy(allCameras = List(10) {
            Camera(
                isFavourite = it % 3 == 0,
                isVisible = it % 2 == 1,
            )
        })
        val allFavourite = cameraState.favouriteCameras

        assertEquals(true, allFavourite.all { it.isFavourite })
    }

    @Test
    fun testHidden() {
        cameraState = cameraState.copy(allCameras = List(10) {
            Camera(
                isFavourite = it % 2 == 0,
                isVisible = it % 3 == 1,
            )
        })
        val allHidden = cameraState.hiddenCameras

        assertEquals(true, allHidden.all { !it.isVisible })
    }

    @Test
    fun testShowSectionIndex() {
        cameraState = cameraState.copy(
            filterMode = FilterMode.HIDDEN,
            sortMode = SortMode.DISTANCE,
            searchMode = SearchMode.NAME,
            viewMode = ViewMode.MAP,
        )
        assertEquals(false, cameraState.showSectionIndex)

        cameraState = cameraState.copy(filterMode = FilterMode.VISIBLE)
        assertEquals(false, cameraState.showSectionIndex)

        cameraState = cameraState.copy(sortMode = SortMode.NAME)
        assertEquals(false, cameraState.showSectionIndex)

        cameraState = cameraState.copy(searchMode = SearchMode.NONE)
        assertEquals(false, cameraState.showSectionIndex)

        cameraState = cameraState.copy(viewMode = ViewMode.LIST)
        assertEquals(true, cameraState.showSectionIndex)
    }

    @Test
    fun testGetDisplayedCameras() {
        val cameras = List(100) { i ->
            val name = when (i % 3) {
                0 -> "hello"
                1 -> "there"
                else -> "world"
            }
            val neighbourhood = when (i % 3) {
                0 -> "town"
                1 -> "borough"
                else -> "city"
            }
            val lat = Random.nextDouble() * 90
            val lon = Random.nextDouble() * 180 - 90
            CameraApiModel(
                nameEn = name,
                neighbourhoodEn = neighbourhood,
                location = LocationApiModel(
                    lat = lat,
                    lon = lon,
                )
            ).toCamera().copy(
                isVisible = Random.nextBoolean(),
                isFavourite = Random.nextBoolean(),
            )
        }
        cameraState = CameraState(allCameras = cameras)
        assertEquals(
            cameraState.getDisplayedCameras(searchText = ""),
            cameras
                .filter { it.isVisible }
                .sortedBy { it.sortableName }
        )

        cameraState = cameraState.copy(
            searchMode = SearchMode.NAME,
            filterMode = FilterMode.FAVOURITE,
            sortMode = SortMode.NEIGHBOURHOOD,
        )
        assertEquals(
            cameraState.getDisplayedCameras(searchText = "l"),
            cameras
                .filter { it.isFavourite }
                .filter { it.name.trim().contains("l", true) }
                .sortedWith(compareBy({ it.neighbourhood }, { it.sortableName }))
        )
    }
}
