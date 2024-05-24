package com.textfield.json.ottawastreetcameras

import android.content.SharedPreferences
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.viewmodels.MainViewModel
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
class MainViewModelUnitTest {

    private var mockPrefs: SharedPreferences = Mockito.mock(SharedPreferences::class.java)

    private var mainViewModel = MainViewModel(prefs = mockPrefs)

    @Before
    fun setup() {
        mainViewModel = MainViewModel(prefs = mockPrefs)
    }

    @Test
    fun testChangeViewMode() {
        mainViewModel.changeViewMode(ViewMode.MAP)
        assertEquals(ViewMode.MAP, mainViewModel.cameraState.value.viewMode)

        mainViewModel.changeViewMode(ViewMode.GALLERY)
        assertEquals(ViewMode.GALLERY, mainViewModel.cameraState.value.viewMode)
    }

    @Test
    fun testChangeSortMode() {
        mainViewModel.changeSortMode(SortMode.NEIGHBOURHOOD)
        assertEquals(SortMode.NEIGHBOURHOOD, mainViewModel.cameraState.value.sortMode)

        mainViewModel.changeSortMode(SortMode.NAME)
        assertEquals(SortMode.NAME, mainViewModel.cameraState.value.sortMode)
    }

    @Test
    fun testChangeFilterMode() {
        mainViewModel.changeFilterMode(FilterMode.FAVOURITE)
        assertEquals(FilterMode.FAVOURITE, mainViewModel.cameraState.value.filterMode)

        mainViewModel.changeFilterMode(FilterMode.HIDDEN)
        assertEquals(FilterMode.HIDDEN, mainViewModel.cameraState.value.filterMode)
    }

    @Test
    fun testChangeSearchMode() {
        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD)
        assertEquals(SearchMode.NEIGHBOURHOOD, mainViewModel.cameraState.value.searchMode)

        mainViewModel.searchCameras(SearchMode.NAME)
        assertEquals(SearchMode.NAME, mainViewModel.cameraState.value.searchMode)
    }

    @Test
    fun testSearchCameras() {
        mainViewModel = MainViewModel(
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

        mainViewModel.searchCameras(SearchMode.NAME, "Camera 5")
        assertEquals(mainViewModel.cameraState.value.displayedCameras.size, 1)

        mainViewModel.searchCameras(SearchMode.NONE, "any")
        assertEquals(mainViewModel.cameraState.value.displayedCameras.size, 10)

        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD, "")
        assertEquals(mainViewModel.cameraState.value.displayedCameras.size, 10)

        mainViewModel.searchCameras(SearchMode.NAME, "")
        assertEquals(mainViewModel.cameraState.value.displayedCameras.size, 10)

        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD, "neighbourhood")
        assertEquals(mainViewModel.cameraState.value.displayedCameras.size, 1)
    }

    @Test
    fun testSelectAllCameras() {
        mainViewModel = MainViewModel(
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

        mainViewModel.selectAllCameras()

        assertNotEquals(0, mainViewModel.cameraState.value.selectedCameras.size)
        assertEquals(
            mainViewModel.cameraState.value.displayedCameras.size,
            mainViewModel.cameraState.value.selectedCameras.size
        )
    }

    @Test
    fun testClearSelectedCameras() {
        mainViewModel = MainViewModel(
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

        mainViewModel.selectAllCameras()
        assertNotEquals(0, mainViewModel.cameraState.value.selectedCameras.size)

        mainViewModel.selectAllCameras(false)
        assertEquals(0, mainViewModel.cameraState.value.selectedCameras.size)
    }

    @Test
    fun testSelectCamera() {
        mainViewModel = MainViewModel(
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

        assertTrue(mainViewModel.cameraState.value.selectedCameras.isEmpty())

        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras.first())

        assertFalse(mainViewModel.cameraState.value.selectedCameras.isEmpty())
    }

    @Test
    fun testFavouriteSelectedCameras() {
        mainViewModel = MainViewModel(
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

        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[1])
        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[4])

        mainViewModel.favouriteCameras(mainViewModel.cameraState.value.selectedCameras)

        assertTrue(mainViewModel.cameraState.value.allCameras[1].isFavourite)
        assertTrue(mainViewModel.cameraState.value.allCameras[4].isFavourite)
    }

    @Test
    fun testHideSelectedCameras() {
        mainViewModel = MainViewModel(
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

        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[5])
        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[8])

        mainViewModel.hideCameras(mainViewModel.cameraState.value.selectedCameras)

        assertFalse(mainViewModel.cameraState.value.allCameras[5].isVisible)
        assertFalse(mainViewModel.cameraState.value.allCameras[8].isVisible)
    }
}