package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.network.model.CameraApiModel
import com.textfield.json.ottawastreetcameras.network.model.LocationApiModel
import com.textfield.json.ottawastreetcameras.ui.main.CameraState
import com.textfield.json.ottawastreetcameras.ui.main.FilterMode
import com.textfield.json.ottawastreetcameras.ui.main.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.main.SearchMode
import com.textfield.json.ottawastreetcameras.ui.main.SortMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelUnitTest {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var fakeCameraRepository: FakeCameraRepository
    private lateinit var prefs: FakePreferences

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakeCameraRepository = FakeCameraRepository()
        prefs = FakePreferences()
        mainViewModel = MainViewModel(
            cameraRepository = fakeCameraRepository,
            prefs = prefs,
            _uiState = MutableStateFlow(CameraState()),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testChangeViewMode() = runTest {
        mainViewModel.changeViewMode(ViewMode.MAP)
        advanceUntilIdle()
        assertEquals(ViewMode.MAP, mainViewModel.uiState.value.viewMode)

        mainViewModel.changeViewMode(ViewMode.GALLERY)
        advanceUntilIdle()
        assertEquals(ViewMode.GALLERY, mainViewModel.uiState.value.viewMode)
    }

    @Test
    fun testChangeSortMode() {
        mainViewModel.changeSortMode(SortMode.NEIGHBOURHOOD)
        assertEquals(SortMode.NEIGHBOURHOOD, mainViewModel.uiState.value.sortMode)

        mainViewModel.changeSortMode(SortMode.NAME)
        assertEquals(SortMode.NAME, mainViewModel.uiState.value.sortMode)
    }

    @Test
    fun testChangeFilterMode() {
        mainViewModel.changeFilterMode(FilterMode.FAVOURITE)
        assertEquals(FilterMode.FAVOURITE, mainViewModel.uiState.value.filterMode)

        mainViewModel.changeFilterMode(FilterMode.HIDDEN)
        assertEquals(FilterMode.HIDDEN, mainViewModel.uiState.value.filterMode)
    }

    @Test
    fun testChangeSearchMode() = runTest {
        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD)
        advanceUntilIdle()
        assertEquals(SearchMode.NEIGHBOURHOOD, mainViewModel.uiState.value.searchMode)

        mainViewModel.searchCameras(SearchMode.NAME)
        advanceUntilIdle()
        assertEquals(SearchMode.NAME, mainViewModel.uiState.value.searchMode)
    }

    @Test
    fun testSearchCameras() = runTest {
        mainViewModel = MainViewModel(
            cameraRepository = fakeCameraRepository,
            prefs = prefs,
            _uiState = MutableStateFlow(
                CameraState(
                    allCameras = List(10) {
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
        advanceUntilIdle()
        assertEquals(
            true,
            mainViewModel.uiState.value.getDisplayedCameras("Camera 5")
                .all { it.name.contains("Camera 5") })

        mainViewModel.searchCameras(SearchMode.NONE, "any")
        advanceUntilIdle()
        assertEquals(
            mainViewModel.uiState.value.allCameras.size,
            mainViewModel.uiState.value.getDisplayedCameras("any").size
        )

        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD, "")
        advanceUntilIdle()
        assertEquals(
            mainViewModel.uiState.value.allCameras.size,
            mainViewModel.uiState.value.getDisplayedCameras("").size
        )

        mainViewModel.searchCameras(SearchMode.NAME, "")
        advanceUntilIdle()
        assertEquals(
            mainViewModel.uiState.value.allCameras.size,
            mainViewModel.uiState.value.getDisplayedCameras("").size
        )

        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD, "neighbourhood")
        advanceUntilIdle()
        assertEquals(
            true,
            mainViewModel.uiState.value.getDisplayedCameras("neighbourhood")
                .all { it.neighbourhood.contains("neighbourhood") })
    }

    @Test
    fun testSelectAllCameras() {
        val allCameras = List(10) {
            CameraApiModel(
                nameEn = "Camera $it",
                location = LocationApiModel(
                    lat = Random.nextDouble() * 90,
                    lon = Random.nextDouble() * 180 - 90,
                ),
            ).toCamera()
        }
        mainViewModel = MainViewModel(
            cameraRepository = fakeCameraRepository,
            prefs = prefs,
            _uiState = MutableStateFlow(
                CameraState(allCameras = allCameras),
            ),
        )

        mainViewModel.selectAllCameras()

        assertNotEquals(0, mainViewModel.uiState.value.selectedCameras.size)
        assertEquals(
            mainViewModel.uiState.value.allCameras.size,
            mainViewModel.uiState.value.selectedCameras.size
        )
    }

    @Test
    fun testClearSelectedCameras() {
        val allCameras = List(10) {
            CameraApiModel(
                nameEn = "Camera $it",
                location = LocationApiModel(
                    lat = Random.nextDouble() * 90,
                    lon = Random.nextDouble() * 180 - 90,
                ),
            ).toCamera()
        }
        mainViewModel = MainViewModel(
            cameraRepository = fakeCameraRepository,
            prefs = prefs,
            _uiState = MutableStateFlow(
                CameraState(
                    allCameras = allCameras,
                ),
            ),
        )

        mainViewModel.selectAllCameras()
        assertNotEquals(0, mainViewModel.uiState.value.selectedCameras.size)

        mainViewModel.selectAllCameras(false)
        assertEquals(0, mainViewModel.uiState.value.selectedCameras.size)
    }

    @Test
    fun testSelectCamera() {
        val allCameras = List(10) {
            CameraApiModel(
                nameEn = "Camera $it",
                location = LocationApiModel(
                    lat = Random.nextDouble() * 90,
                    lon = Random.nextDouble() * 180 - 90,
                ),
            ).toCamera()
        }
        mainViewModel = MainViewModel(
            cameraRepository = fakeCameraRepository,
            prefs = prefs,
            _uiState = MutableStateFlow(CameraState(allCameras = allCameras)),
        )

        assertEquals(true, mainViewModel.uiState.value.selectedCameras.isEmpty())

        mainViewModel.selectCamera(mainViewModel.uiState.value.allCameras.first())

        assertEquals(false, mainViewModel.uiState.value.selectedCameras.isEmpty())
    }

    @Test
    fun testFavouriteSelectedCameras() = runTest {
        val allCameras = List(10) {
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
            cameraRepository = fakeCameraRepository,
            prefs = prefs,
            _uiState = MutableStateFlow(CameraState(allCameras = allCameras)),
        )

        mainViewModel.selectCamera(mainViewModel.uiState.value.allCameras[1])
        mainViewModel.selectCamera(mainViewModel.uiState.value.allCameras[4])

        mainViewModel.favouriteCameras(mainViewModel.uiState.value.selectedCameras)
        advanceUntilIdle()
        assertEquals(true, mainViewModel.uiState.value.allCameras[1].isFavourite)
        assertEquals(true, mainViewModel.uiState.value.allCameras[4].isFavourite)
    }

    @Test
    fun testHideSelectedCameras() = runTest {
        val allCameras = List(10) {
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
            cameraRepository = fakeCameraRepository,
            prefs = prefs,
            _uiState = MutableStateFlow(CameraState(allCameras = allCameras)),
        )

        assertEquals(true, mainViewModel.uiState.value.allCameras[5].isVisible)
        assertEquals(true, mainViewModel.uiState.value.allCameras[8].isVisible)

        mainViewModel.selectCamera(mainViewModel.uiState.value.allCameras[5])
        mainViewModel.selectCamera(mainViewModel.uiState.value.allCameras[8])

        mainViewModel.hideCameras(mainViewModel.uiState.value.selectedCameras)
        advanceUntilIdle()

        assertEquals(false, mainViewModel.uiState.value.allCameras[5].isVisible)
        assertEquals(false, mainViewModel.uiState.value.allCameras[8].isVisible)
    }

    @Test
    fun `test resetFilters`() = runTest {
        mainViewModel.changeFilterMode(FilterMode.FAVOURITE)
        mainViewModel.searchCameras(SearchMode.NAME, "")
        advanceUntilIdle()
        mainViewModel.resetFilters()
        advanceUntilIdle()
        assertEquals(FilterMode.VISIBLE, mainViewModel.uiState.value.filterMode)
        assertEquals(SearchMode.NONE, mainViewModel.uiState.value.searchMode)
    }

    @Test
    fun `test unapply filterMode`() = runTest {
        mainViewModel.changeFilterMode(FilterMode.FAVOURITE)
        advanceUntilIdle()
        mainViewModel.changeFilterMode(FilterMode.FAVOURITE)
        advanceUntilIdle()
        assertEquals(FilterMode.VISIBLE, mainViewModel.uiState.value.filterMode)
    }
}