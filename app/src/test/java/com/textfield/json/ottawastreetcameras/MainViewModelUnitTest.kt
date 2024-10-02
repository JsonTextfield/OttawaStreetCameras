package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.data.CameraDataSource
import com.textfield.json.ottawastreetcameras.data.CameraRepositoryImpl
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.CameraApiModel
import com.textfield.json.ottawastreetcameras.entities.LocationApiModel
import com.textfield.json.ottawastreetcameras.ui.main.CameraState
import com.textfield.json.ottawastreetcameras.ui.main.FilterMode
import com.textfield.json.ottawastreetcameras.ui.main.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.main.SearchMode
import com.textfield.json.ottawastreetcameras.ui.main.SortMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class MainViewModelUnitTest {

    private lateinit var mainViewModel: MainViewModel

    class FakeCameraDataSource : CameraDataSource {
        override suspend fun getAllCameras(): List<Camera> {
            return (0 until 100).map {
                Camera(
                    id = "$it",
                    _name = BilingualObject("Camera $it", "Camera $it"),
                )
            }
        }
    }

    @Before
    fun setup() {
        mainViewModel = MainViewModel(CameraRepositoryImpl(FakeCameraDataSource()))
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
            cameraRepository = CameraRepositoryImpl(FakeCameraDataSource()),
            _cameraState = MutableStateFlow(
                CameraState(
                    allCameras = (0 until 10).map {
                        CameraApiModel(
                            nameEn = "Camera $it",
                            neighbourhoodEn = if (it == 1) "neighbourhood" else "",
                            location = LocationApiModel(
                                lat = Random.nextDouble() * 90,
                                lon = Random.nextDouble() * 180 - 90,
                            ),
                        ).toCamera()
                    },
                ),
            ),
        )

        mainViewModel.searchCameras(SearchMode.NAME, "Camera 5")
        assertEquals(1, mainViewModel.cameraState.value.displayedCameras.size)

        mainViewModel.searchCameras(SearchMode.NONE, "any")
        assertEquals(10, mainViewModel.cameraState.value.displayedCameras.size)

        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD, "")
        assertEquals(10, mainViewModel.cameraState.value.displayedCameras.size)

        mainViewModel.searchCameras(SearchMode.NAME, "")
        assertEquals(10, mainViewModel.cameraState.value.displayedCameras.size)

        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD, "neighbourhood")
        assertEquals(1, mainViewModel.cameraState.value.displayedCameras.size)
    }

    @Test
    fun testSelectAllCameras() {
        val allCameras = (0 until 10).map {
            CameraApiModel(
                nameEn = "Camera $it",
                location = LocationApiModel(
                    lat = Random.nextDouble() * 90,
                    lon = Random.nextDouble() * 180 - 90,
                ),
            ).toCamera()
        }
        mainViewModel = MainViewModel(
            cameraRepository = CameraRepositoryImpl(FakeCameraDataSource()),
            _cameraState = MutableStateFlow(
                CameraState(
                    allCameras = allCameras,
                    displayedCameras = allCameras,
                ),
            ),
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
        val allCameras = (0 until 10).map {
            CameraApiModel(
                nameEn = "Camera $it",
                location = LocationApiModel(
                    lat = Random.nextDouble() * 90,
                    lon = Random.nextDouble() * 180 - 90,
                ),
            ).toCamera()
        }
        mainViewModel = MainViewModel(
            cameraRepository = CameraRepositoryImpl(FakeCameraDataSource()),
            _cameraState = MutableStateFlow(
                CameraState(
                    allCameras = allCameras,
                    displayedCameras = allCameras,
                ),
            ),
        )

        mainViewModel.selectAllCameras()
        assertNotEquals(0, mainViewModel.cameraState.value.selectedCameras.size)

        mainViewModel.selectAllCameras(false)
        assertEquals(0, mainViewModel.cameraState.value.selectedCameras.size)
    }

    @Test
    fun testSelectCamera() {
        val allCameras = (0 until 10).map {
            CameraApiModel(
                nameEn = "Camera $it",
                location = LocationApiModel(
                    lat = Random.nextDouble() * 90,
                    lon = Random.nextDouble() * 180 - 90,
                ),
            ).toCamera()
        }
        mainViewModel = MainViewModel(
            cameraRepository = CameraRepositoryImpl(FakeCameraDataSource()),
            _cameraState = MutableStateFlow(CameraState(allCameras = allCameras)),
        )

        assertTrue(mainViewModel.cameraState.value.selectedCameras.isEmpty())

        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras.first())

        assertFalse(mainViewModel.cameraState.value.selectedCameras.isEmpty())
    }

    @Test
    fun testFavouriteSelectedCameras() {
        val allCameras = (0 until 10).map {
            CameraApiModel(
                id = "$it",
                nameEn = "Camera $it",
                location = LocationApiModel(
                    lat = 45.451235,
                    lon = -75.6742136,
                ),
            ).toCamera()
        }
        mainViewModel = MainViewModel(
            cameraRepository = CameraRepositoryImpl(FakeCameraDataSource()),
            _cameraState = MutableStateFlow(CameraState(allCameras = allCameras)),
        )

        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[1])
        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[4])

        mainViewModel.favouriteCameras(mainViewModel.cameraState.value.selectedCameras)

        assertTrue(mainViewModel.cameraState.value.allCameras[1].isFavourite)
        assertTrue(mainViewModel.cameraState.value.allCameras[4].isFavourite)
    }

    @Test
    fun testHideSelectedCameras() {
        val allCameras = (0 until 10).map {
            CameraApiModel(
                id = "$it",
                nameEn = "Camera $it",
                location = LocationApiModel(
                    lat = Random.nextDouble() * 45.451235,
                    lon = Random.nextDouble() * -75.6742136,
                ),
            ).toCamera()
        }
        mainViewModel = MainViewModel(
            cameraRepository = CameraRepositoryImpl(FakeCameraDataSource()),
            _cameraState = MutableStateFlow(CameraState(allCameras = allCameras)),
        )

        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[5])
        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[8])

        mainViewModel.hideCameras(mainViewModel.cameraState.value.selectedCameras)

        assertFalse(mainViewModel.cameraState.value.allCameras[5].isVisible)
        assertFalse(mainViewModel.cameraState.value.allCameras[8].isVisible)
    }
}