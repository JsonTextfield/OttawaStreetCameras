package com.textfield.json.ottawastreetcameras

import android.content.Context
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CameraViewModelUnitTest {

    @Mock
    private lateinit var mockContext: Context

    private var cameraViewModel = CameraViewModel()

    @Before
    fun setup() {
        cameraViewModel = CameraViewModel()
    }

    @Test
    fun testChangeViewMode() {
        cameraViewModel.changeViewMode(mockContext, ViewMode.MAP)
        assertEquals(ViewMode.MAP, cameraViewModel.cameraState.value.viewMode)

        cameraViewModel.changeViewMode(mockContext, ViewMode.GALLERY)
        assertEquals(ViewMode.GALLERY, cameraViewModel.cameraState.value.viewMode)
    }

    @Test
    fun testChangeSortMode() {
        cameraViewModel.changeSortMode(mockContext, SortMode.NEIGHBOURHOOD)
        assertEquals(SortMode.NEIGHBOURHOOD, cameraViewModel.cameraState.value.sortMode)

        cameraViewModel.changeSortMode(mockContext, SortMode.NAME)
        assertEquals(SortMode.NAME, cameraViewModel.cameraState.value.sortMode)
    }

    @Test
    fun testChangeFilterMode() {
        cameraViewModel.changeFilterMode(FilterMode.FAVOURITE)
        assertEquals(FilterMode.FAVOURITE, cameraViewModel.cameraState.value.filterMode)

        cameraViewModel.changeFilterMode(FilterMode.HIDDEN)
        assertEquals(FilterMode.HIDDEN, cameraViewModel.cameraState.value.filterMode)
    }

    @Test
    fun testChangeSearchMode() {
        cameraViewModel.changeSearchMode(SearchMode.NEIGHBOURHOOD)
        assertEquals(SearchMode.NEIGHBOURHOOD, cameraViewModel.cameraState.value.searchMode)

        cameraViewModel.changeSearchMode(SearchMode.NAME)
        assertEquals(SearchMode.NAME, cameraViewModel.cameraState.value.searchMode)
    }

    @Test
    fun testSearchCameras() {
        cameraViewModel = CameraViewModel(_cameraState = MutableStateFlow(CameraState().apply {
            allCameras.addAll((0 until 10).map {
                Camera(JSONObject().apply {
                    put("description", "Camera $it")
                }).apply {
                    if (it == 1) {
                        neighbourhood = "neighbourhood"
                    }
                }
            })
        }))

        cameraViewModel.changeSearchMode(SearchMode.NAME)
        cameraViewModel.searchCameras("Camera 5")
        assertEquals(cameraViewModel.cameraState.value.displayedCameras.size, 1)

        cameraViewModel.changeSearchMode(SearchMode.NONE)
        cameraViewModel.searchCameras("any")
        assertEquals(cameraViewModel.cameraState.value.displayedCameras.size, 10)

        cameraViewModel.changeSearchMode(SearchMode.NEIGHBOURHOOD)
        cameraViewModel.searchCameras("")
        assertEquals(cameraViewModel.cameraState.value.displayedCameras.size, 10)

        cameraViewModel.changeSearchMode(SearchMode.NAME)
        cameraViewModel.searchCameras("")
        assertEquals(cameraViewModel.cameraState.value.displayedCameras.size, 10)

        cameraViewModel.changeSearchMode(SearchMode.NEIGHBOURHOOD)
        cameraViewModel.searchCameras("neighbourhood")
        assertEquals(cameraViewModel.cameraState.value.displayedCameras.size, 1)
    }

    @Test
    fun testSelectAllCameras() {
        cameraViewModel = CameraViewModel(_cameraState = MutableStateFlow(CameraState().apply {
            allCameras.addAll((0 until 10).map {
                Camera(JSONObject().apply {
                    put("description", "Camera $it")
                    put("number", it)
                })
            })
            displayedCameras = allCameras
        }))

        cameraViewModel.selectAllCameras()

        assertNotEquals(0, cameraViewModel.cameraState.value.selectedCameras.size)
        assertEquals(
            cameraViewModel.cameraState.value.displayedCameras.size,
            cameraViewModel.cameraState.value.selectedCameras.size
        )
    }

    @Test
    fun testClearSelectedCameras() {
        cameraViewModel = CameraViewModel(_cameraState = MutableStateFlow(CameraState().apply {
            allCameras.addAll((0 until 10).map {
                Camera(JSONObject().apply {
                    put("description", "Camera $it")
                    put("number", it)
                })
            })
            displayedCameras = allCameras
        }))

        cameraViewModel.selectAllCameras()
        assertNotEquals(0, cameraViewModel.cameraState.value.selectedCameras.size)

        cameraViewModel.clearSelectedCameras()
        assertEquals(0, cameraViewModel.cameraState.value.selectedCameras.size)
    }

    @Test
    fun testSelectCamera() {
        cameraViewModel = CameraViewModel(_cameraState = MutableStateFlow(CameraState().apply {
            allCameras.addAll((0 until 10).map {
                Camera(JSONObject().apply {
                    put("description", "Camera $it")
                    put("number", it)
                })
            })
        }))

        assertTrue(cameraViewModel.cameraState.value.selectedCameras.isEmpty())

        cameraViewModel.selectCamera(cameraViewModel.cameraState.value.allCameras.first())

        assertFalse(cameraViewModel.cameraState.value.selectedCameras.isEmpty())
    }

    @Test
    fun testFavouriteSelectedCameras() {
        cameraViewModel = CameraViewModel(_cameraState = MutableStateFlow(CameraState().apply {
            allCameras.addAll((0 until 10).map {
                Camera(JSONObject().apply {
                    put("description", "Camera $it")
                    put("number", it)
                })
            })
        }))

        cameraViewModel.selectCamera(cameraViewModel.cameraState.value.allCameras[1])
        cameraViewModel.selectCamera(cameraViewModel.cameraState.value.allCameras[4])

        cameraViewModel.favouriteSelectedCameras(mockContext, true)

        assertTrue(cameraViewModel.cameraState.value.allCameras[1].isFavourite)
        assertTrue(cameraViewModel.cameraState.value.allCameras[4].isFavourite)
    }

    @Test
    fun testHideSelectedCameras() {
        cameraViewModel = CameraViewModel(_cameraState = MutableStateFlow(CameraState().apply {
            allCameras.addAll((0 until 10).map {
                Camera(JSONObject().apply {
                    put("description", "Camera $it")
                    put("number", it)
                })
            })
        }))

        cameraViewModel.selectCamera(cameraViewModel.cameraState.value.allCameras[5])
        cameraViewModel.selectCamera(cameraViewModel.cameraState.value.allCameras[8])

        cameraViewModel.hideSelectedCameras(mockContext, false)

        assertFalse(cameraViewModel.cameraState.value.allCameras[5].isVisible)
        assertFalse(cameraViewModel.cameraState.value.allCameras[8].isVisible)
    }
}