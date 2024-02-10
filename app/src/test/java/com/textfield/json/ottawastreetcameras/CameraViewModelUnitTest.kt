package com.textfield.json.ottawastreetcameras

import android.content.SharedPreferences
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
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlin.random.Random

@RunWith(MockitoJUnitRunner::class)
class CameraViewModelUnitTest {

    private var mockPrefs: SharedPreferences = Mockito.mock(SharedPreferences::class.java)

    private var cameraViewModel = CameraViewModel(prefs = mockPrefs)

    @Before
    fun setup() {
        cameraViewModel = CameraViewModel(prefs = mockPrefs)
    }

    @Test
    fun testChangeViewMode() {
        cameraViewModel.changeViewMode(ViewMode.MAP)
        assertEquals(ViewMode.MAP, cameraViewModel.cameraState.value.viewMode)

        cameraViewModel.changeViewMode(ViewMode.GALLERY)
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
        cameraViewModel = CameraViewModel(
            _cameraState = MutableStateFlow(CameraState().apply {
                allCameras = (0 until 10).map {
                    Camera.fromJson(JSONObject().apply {
                        val location = JSONObject()
                        location.put("lat", Random.nextDouble() * 90)
                        location.put("lon", Random.nextDouble() * 180 - 90)
                        put("location", location)
                        put("nameEn", "Camera $it")
                        if (it == 1) {
                            put("neighbourhoodEn", "neighbourhood")
                        }
                    })
                }
            }),
            prefs = mockPrefs,
        )

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
        cameraViewModel = CameraViewModel(
            _cameraState = MutableStateFlow(CameraState().apply {
                allCameras = (0 until 10).map {
                    Camera.fromJson(JSONObject().apply {
                        val location = JSONObject()
                        location.put("lat", Random.nextDouble() * 90)
                        location.put("lon", Random.nextDouble() * 180 - 90)
                        put("location", location)
                        put("nameEn", "Camera $it")
                    })
                }
                displayedCameras = allCameras
            }),
            prefs = mockPrefs,
        )

        cameraViewModel.selectAllCameras()

        assertNotEquals(0, cameraViewModel.cameraState.value.selectedCameras.size)
        assertEquals(
            cameraViewModel.cameraState.value.displayedCameras.size,
            cameraViewModel.cameraState.value.selectedCameras.size
        )
    }

    @Test
    fun testClearSelectedCameras() {
        cameraViewModel = CameraViewModel(
            _cameraState = MutableStateFlow(CameraState().apply {
                allCameras = (0 until 10).map {
                    Camera.fromJson(JSONObject().apply {
                        val location = JSONObject()
                        location.put("lat", Random.nextDouble() * 90)
                        location.put("lon", Random.nextDouble() * 180 - 90)
                        put("location", location)
                        put("nameEn", "Camera $it")
                    })
                }
                displayedCameras = allCameras
            }),
            prefs = mockPrefs,
        )

        cameraViewModel.selectAllCameras()
        assertNotEquals(0, cameraViewModel.cameraState.value.selectedCameras.size)

        cameraViewModel.selectAllCameras(false)
        assertEquals(0, cameraViewModel.cameraState.value.selectedCameras.size)
    }

    @Test
    fun testSelectCamera() {
        cameraViewModel = CameraViewModel(
            _cameraState = MutableStateFlow(CameraState().apply {
                allCameras = (0 until 10).map {
                    Camera.fromJson(JSONObject().apply {
                        val location = JSONObject()
                        location.put("lat", Random.nextDouble() * 90)
                        location.put("lon", Random.nextDouble() * 180 - 90)
                        put("location", location)
                        put("nameEn", "Camera $it")
                    })
                }
            }),
            prefs = mockPrefs,
        )

        assertTrue(cameraViewModel.cameraState.value.selectedCameras.isEmpty())

        cameraViewModel.selectCamera(cameraViewModel.cameraState.value.allCameras.first())

        assertFalse(cameraViewModel.cameraState.value.selectedCameras.isEmpty())
    }

    @Test
    fun testFavouriteSelectedCameras() {
        cameraViewModel = CameraViewModel(
            _cameraState = MutableStateFlow(CameraState().apply {
                allCameras = (0 until 10).map {
                    Camera.fromJson(JSONObject().apply {
                        val location = JSONObject()
                        put("id", "$it")
                        location.put("lat", 45.451235)
                        location.put("lon", -75.6742136)
                        put("location", location)
                        put("nameEn", "Camera $it")
                    })
                }
            }),
            prefs = mockPrefs,
        )

        cameraViewModel.selectCamera(cameraViewModel.cameraState.value.allCameras[1])
        cameraViewModel.selectCamera(cameraViewModel.cameraState.value.allCameras[4])

        cameraViewModel.favouriteCameras(cameraViewModel.cameraState.value.selectedCameras)

        assertTrue(cameraViewModel.cameraState.value.allCameras[1].isFavourite)
        assertTrue(cameraViewModel.cameraState.value.allCameras[4].isFavourite)
    }

    @Test
    fun testHideSelectedCameras() {
        cameraViewModel = CameraViewModel(
            _cameraState = MutableStateFlow(CameraState().apply {
                allCameras = (0 until 10).map {
                    Camera.fromJson(JSONObject().apply {
                        put("id", "$it")
                        val location = JSONObject()
                        location.put("lat", 45.451235)
                        location.put("lon", -75.6742136)
                        put("location", location)
                        put("nameEn", "Camera $it")
                    })
                }
            }),
            prefs = mockPrefs,
        )

        cameraViewModel.selectCamera(cameraViewModel.cameraState.value.allCameras[5])
        cameraViewModel.selectCamera(cameraViewModel.cameraState.value.allCameras[8])

        cameraViewModel.hideCameras(cameraViewModel.cameraState.value.selectedCameras)

        assertFalse(cameraViewModel.cameraState.value.allCameras[5].isVisible)
        assertFalse(cameraViewModel.cameraState.value.allCameras[8].isVisible)
    }
}