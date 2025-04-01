package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

@Composable
fun MainContent(
    mainViewModel: MainViewModel,
    listState: LazyListState,
    gridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
    onNavigateToCameraScreen: (List<Camera>) -> Unit = {},
) {
    val cameraState by mainViewModel.cameraState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val onItemClick = { camera: Camera ->
        if (cameraState.selectedCameras.isNotEmpty()) {
            mainViewModel.selectCamera(camera)
        }
        else {
            onNavigateToCameraScreen(listOf(camera))
        }
    }
    val onItemLongClick = { camera: Camera -> mainViewModel.selectCamera(camera) }
    var dismissedCamera by remember { mutableStateOf<Camera?>(null) }
    LaunchedEffect(dismissedCamera) {
        dismissedCamera?.let {
            mainViewModel.hideCameras(listOf(it))
            val visibilityStringId = if (it.isVisible) R.string.hidden else R.string.unhidden
            val result = snackbarHostState.showSnackbar(
                message = context.getString(
                    R.string.camera_visibility_changed,
                    it.name,
                    context.getString(visibilityStringId)
                ),
                actionLabel = context.getString(R.string.undo),
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                mainViewModel.hideCameras(listOf(it))
            }
            dismissedCamera = null
        }
    }
    Column {
//        FilterChipStrip(
//            filterMode = cameraState.filterMode,
//            onChangeFilterMode = mainViewModel::changeFilterMode,
//        )
        when (cameraState.viewMode) {
            ViewMode.LIST -> {
                CameraListItemList(
                    cameraState = cameraState,
                    listState = listState,
                    onItemClick = onItemClick,
                    onItemLongClick = onItemLongClick,
                    onItemDismissed = { dismissedCamera = it },
                    onFavouriteClick = { mainViewModel.favouriteCameras(listOf(it)) },
                )
            }

            ViewMode.MAP -> {
                CameraMapView(
                    cameraState = cameraState,
                    onItemClick = onItemClick,
                    onItemLongClick = onItemLongClick,
                )
            }

            ViewMode.GALLERY -> {
                CameraGalleryView(
                    cameraState = cameraState,
                    gridState = gridState,
                    onItemClick = onItemClick,
                    onItemLongClick = onItemLongClick,
                )
            }
        }
    }
}
