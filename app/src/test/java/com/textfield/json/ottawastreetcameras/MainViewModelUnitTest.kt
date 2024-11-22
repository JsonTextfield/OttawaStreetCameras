package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.data.CameraRepository
import com.textfield.json.ottawastreetcameras.data.ICameraDataSource
import com.textfield.json.ottawastreetcameras.data.IPreferencesRepository
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.CameraApiModel
import com.textfield.json.ottawastreetcameras.entities.LocationApiModel
import com.textfield.json.ottawastreetcameras.ui.main.CameraState
import com.textfield.json.ottawastreetcameras.ui.main.FilterMode
import com.textfield.json.ottawastreetcameras.ui.main.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.main.SearchMode
import com.textfield.json.ottawastreetcameras.ui.main.SortMode
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelUnitTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var mainViewModel: MainViewModel

    class FakeCameraDataSource : ICameraDataSource {
        override suspend fun getAllCameras(): List<Camera> {
            return (0 until 100).map {
                Camera(
                    id = "$it",
                    _name = BilingualObject("Camera $it", "Camera $it"),
                )
            }
        }
    }

    class FakePreferences : IPreferencesRepository {
        private val data = mutableMapOf<String, Any>()
        override suspend fun favourite(id: String, value: Boolean) {
            data[id] = value
        }

        override suspend fun isFavourite(id: String): Boolean {
            return data[id] as Boolean
        }

        override suspend fun setVisibility(id: String, value: Boolean) {
            data[id] = value
        }

        override suspend fun isVisible(id: String): Boolean {
            return data[id] as Boolean
        }

        override suspend fun setTheme(theme: ThemeMode) {
            data["theme"] = theme
        }

        override suspend fun getTheme(): ThemeMode {
            return (data["theme"] ?: ThemeMode.SYSTEM) as ThemeMode
        }

        override suspend fun setViewMode(viewMode: ViewMode) {
            data["viewMode"] = viewMode
        }

        override suspend fun getViewMode(): ViewMode {
            return data["viewMode"] as ViewMode
        }

    }

    @Before
    fun setup() {
        mainViewModel = MainViewModel(
            cameraRepository = CameraRepository(FakeCameraDataSource()),
            prefs = FakePreferences(),
            dispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun testChangeViewMode() = runTest {
        mainViewModel.changeViewMode(ViewMode.MAP)
        advanceUntilIdle()
        assertEquals(ViewMode.MAP, mainViewModel.cameraState.value.viewMode)

        mainViewModel.changeViewMode(ViewMode.GALLERY)
        advanceUntilIdle()
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
    fun testChangeSearchMode() = runTest{
        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD)
        advanceUntilIdle()
        assertEquals(SearchMode.NEIGHBOURHOOD, mainViewModel.cameraState.value.searchMode)

        mainViewModel.searchCameras(SearchMode.NAME)
        advanceUntilIdle()
        assertEquals(SearchMode.NAME, mainViewModel.cameraState.value.searchMode)
    }

    @Test
    fun testSearchCameras() = runTest{
        mainViewModel = MainViewModel(
            cameraRepository = CameraRepository(FakeCameraDataSource()),
            prefs = FakePreferences(),
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
            dispatcher = UnconfinedTestDispatcher(),
        )

        mainViewModel.searchCameras(SearchMode.NAME, "Camera 5")
        advanceUntilIdle()
        assertEquals(1, mainViewModel.cameraState.value.displayedCameras.size)

        mainViewModel.searchCameras(SearchMode.NONE, "any")
        advanceUntilIdle()
        assertEquals(10, mainViewModel.cameraState.value.displayedCameras.size)

        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD, "")
        advanceUntilIdle()
        assertEquals(10, mainViewModel.cameraState.value.displayedCameras.size)

        mainViewModel.searchCameras(SearchMode.NAME, "")
        advanceUntilIdle()
        assertEquals(10, mainViewModel.cameraState.value.displayedCameras.size)

        mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD, "neighbourhood")
        advanceUntilIdle()
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
            cameraRepository = CameraRepository(FakeCameraDataSource()),
            prefs = FakePreferences(),
            _cameraState = MutableStateFlow(
                CameraState(
                    allCameras = allCameras,
                    displayedCameras = allCameras,
                ),
            ),
            dispatcher = UnconfinedTestDispatcher(),
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
            cameraRepository = CameraRepository(FakeCameraDataSource()),
            prefs = FakePreferences(),
            _cameraState = MutableStateFlow(
                CameraState(
                    allCameras = allCameras,
                    displayedCameras = allCameras,
                ),
            ),
            dispatcher = UnconfinedTestDispatcher(),
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
            cameraRepository = CameraRepository(FakeCameraDataSource()),
            prefs = FakePreferences(),
            _cameraState = MutableStateFlow(CameraState(allCameras = allCameras)),
            dispatcher = UnconfinedTestDispatcher(),
        )

        assertTrue(mainViewModel.cameraState.value.selectedCameras.isEmpty())

        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras.first())

        assertFalse(mainViewModel.cameraState.value.selectedCameras.isEmpty())
    }

    @Test
    fun testFavouriteSelectedCameras() = runTest {
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
            cameraRepository = CameraRepository(FakeCameraDataSource()),
            prefs = FakePreferences(),
            _cameraState = MutableStateFlow(CameraState(allCameras = allCameras)),
            dispatcher = UnconfinedTestDispatcher(),
        )

        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[1])
        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[4])

        mainViewModel.favouriteCameras(mainViewModel.cameraState.value.selectedCameras)
        advanceUntilIdle()
        assertTrue(mainViewModel.cameraState.value.allCameras[1].isFavourite)
        assertTrue(mainViewModel.cameraState.value.allCameras[4].isFavourite)
    }

    @Test
    fun testHideSelectedCameras() = runTest {
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
            cameraRepository = CameraRepository(FakeCameraDataSource()),
            prefs = FakePreferences(),
            _cameraState = MutableStateFlow(CameraState(allCameras = allCameras)),
            dispatcher = UnconfinedTestDispatcher(),
        )

        assertTrue(mainViewModel.cameraState.value.allCameras[5].isVisible)
        assertTrue(mainViewModel.cameraState.value.allCameras[8].isVisible)

        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[5])
        mainViewModel.selectCamera(mainViewModel.cameraState.value.allCameras[8])

        mainViewModel.hideCameras(mainViewModel.cameraState.value.selectedCameras)
        advanceUntilIdle()

        assertFalse(mainViewModel.cameraState.value.allCameras[5].isVisible)
        assertFalse(mainViewModel.cameraState.value.allCameras[8].isVisible)
    }
}