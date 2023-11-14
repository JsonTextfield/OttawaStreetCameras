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
        cameraViewModel.changeSortMode(SortMode.NEIGHBOURHOOD)
        assertEquals(SortMode.NEIGHBOURHOOD, cameraViewModel.cameraState.value.sortMode)

        cameraViewModel.changeSortMode(SortMode.NAME)
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
        cameraViewModel.searchCameras(SearchMode.NEIGHBOURHOOD)
        assertEquals(SearchMode.NEIGHBOURHOOD, cameraViewModel.cameraState.value.searchMode)

        cameraViewModel.searchCameras(SearchMode.NAME)
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

        cameraViewModel.searchCameras(SearchMode.NAME, "Camera 5")
        assertEquals(cameraViewModel.cameraState.value.displayedCameras.size, 1)

        cameraViewModel.searchCameras(SearchMode.NONE, "any")
        assertEquals(cameraViewModel.cameraState.value.displayedCameras.size, 10)

        cameraViewModel.searchCameras(SearchMode.NEIGHBOURHOOD, "")
        assertEquals(cameraViewModel.cameraState.value.displayedCameras.size, 10)

        cameraViewModel.searchCameras(SearchMode.NAME, "")
        assertEquals(cameraViewModel.cameraState.value.displayedCameras.size, 10)

        cameraViewModel.searchCameras(SearchMode.NEIGHBOURHOOD, "neighbourhood")
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

        cameraViewModel.selectAllCameras(false)
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

        cameraViewModel.favouriteCameras(mockContext, cameraViewModel.cameraState.value.selectedCameras)

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

        cameraViewModel.hideCameras(mockContext, cameraViewModel.cameraState.value.selectedCameras)

        assertFalse(cameraViewModel.cameraState.value.allCameras[5].isVisible)
        assertFalse(cameraViewModel.cameraState.value.allCameras[8].isVisible)
    }
}