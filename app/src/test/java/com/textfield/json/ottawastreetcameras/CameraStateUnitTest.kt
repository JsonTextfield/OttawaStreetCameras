package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.entities.Camera
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class CameraStateUnitTest {
    private var cameraState = CameraState()

    @Before
    fun setup() {
        cameraState = CameraState()
    }

    @Test
    fun testInitialState() {
        assertEquals(cameraState, CameraState())
        assertEquals(cameraState.allCameras, listOf<Camera>())
        assertEquals(cameraState.displayedCameras, listOf<Camera>())
        assertEquals(cameraState.uiState, UIState.INITIAL)
        assertEquals(cameraState.sortMode, SortMode.NAME)
        assertEquals(cameraState.searchMode, SearchMode.NONE)
        assertEquals(cameraState.searchText, "")
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
        cameraState.allCameras = List(10) {
            Camera().apply {
                isFavourite = it % 3 == 0
                isVisible = it % 2 == 1
            }
        }
        val allFavourite = cameraState.favouriteCameras

        assertTrue(allFavourite.all { it.isFavourite })
    }

    @Test
    fun testHidden() {
        cameraState.allCameras = List(10) {
            Camera().apply {
                isFavourite = it % 2 == 0
                isVisible = it % 3 == 1
            }
        }
        val allHidden = cameraState.hiddenCameras

        assertTrue(allHidden.all { !it.isVisible })
    }

    @Test
    fun testShowSectionIndex() {
        cameraState.filterMode = FilterMode.HIDDEN
        cameraState.sortMode = SortMode.DISTANCE
        cameraState.searchMode = SearchMode.NAME
        cameraState.viewMode = ViewMode.MAP

        assertEquals(false, cameraState.showSectionIndex)

        cameraState.filterMode = FilterMode.VISIBLE
        assertEquals(false, cameraState.showSectionIndex)

        cameraState.sortMode = SortMode.NAME
        assertEquals(false, cameraState.showSectionIndex)

        cameraState.searchMode = SearchMode.NONE
        assertEquals(false, cameraState.showSectionIndex)

        cameraState.viewMode = ViewMode.LIST
        assertEquals(true, cameraState.showSectionIndex)
    }

    @Test
    fun testGetDisplayedCameras() {
        val cameras = List(100) { i ->
            val jsonObject = JSONObject()
            jsonObject.put("latitude", Random.nextDouble() * 90)
            jsonObject.put("longitude", Random.nextDouble() * 180 - 90)
            val name = when (i % 3) {
                0 -> "hello"
                1 -> "there"
                else -> "world"
            }
            jsonObject.put("description", name)
            Camera(jsonObject).apply {
                isVisible = Random.nextBoolean()
                isFavourite = Random.nextBoolean()
                neighbourhood = when (i % 3) {
                    0 -> "town"
                    1 -> "borough"
                    else -> "city"
                }
            }
        }
        cameraState = CameraState(allCameras = cameras)
        assertEquals(cameraState.getDisplayedCameras(), cameras.filter { it.isVisible }.sortedWith(SortByName()))

        cameraState = cameraState.copy(
            searchMode = SearchMode.NAME,
            searchText = "l",
            filterMode = FilterMode.FAVOURITE,
            sortMode = SortMode.NEIGHBOURHOOD,
        )
        assertEquals(cameraState.getDisplayedCameras(),
            cameras.filter { it.isFavourite }.filter { it.name.trim().contains("l", true) }
                .sortedWith(SortByNeighbourhood())
        )
    }
}
