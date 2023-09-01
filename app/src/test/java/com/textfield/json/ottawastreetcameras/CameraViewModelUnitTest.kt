package com.textfield.json.ottawastreetcameras

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CameraViewModelUnitTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockDownloadService: DownloadService

    private var cameraViewModel = CameraViewModel()

    @Before
    fun setup() {
        cameraViewModel = CameraViewModel()
    }


    @Test
    fun testDownloadAll() {
        cameraViewModel = CameraViewModel(downloadService = mockDownloadService)
        cameraViewModel.downloadAll(mockContext)
        //val cameraManager = CameraManager(downloadService = DownloadService)
        //cameraManager.downloadAll()
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
}