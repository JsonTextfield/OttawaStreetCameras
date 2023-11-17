package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CameraStateUnitTest {
    private var cameraState = CameraState()

    @Before
    fun setup() {
        cameraState = CameraState()
    }

    @Test
    fun testFavourites() {
        cameraState.allCameras = (0 until 10).map {
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
        cameraState.allCameras = (0 until 10).map {
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
}
