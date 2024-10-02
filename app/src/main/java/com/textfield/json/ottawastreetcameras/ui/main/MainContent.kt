package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.launch

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
        } else {
            onNavigateToCameraScreen(listOf(camera))
        }
    }
    val onItemLongClick = { camera: Camera -> mainViewModel.selectCamera(camera) }
    val scope = rememberCoroutineScope()
    val onItemDismissed: (Camera) -> Unit = { camera ->
        mainViewModel.hideCameras(listOf(camera))
        val visibilityStringId = if (camera.isVisible) R.string.unhidden else R.string.hidden
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = context.getString(
                    R.string.camera_visibility_changed,
                    camera.name,
                    context.getString(visibilityStringId)
                ),
                actionLabel = context.getString(R.string.undo),
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                mainViewModel.hideCameras(listOf(camera))
            }
        }
    }
    when (cameraState.viewMode) {
        ViewMode.LIST -> {
            CameraListItemList(
                cameraState = cameraState,
                listState = listState,
                onItemClick = onItemClick,
                onItemLongClick = onItemLongClick,
                onItemDismissed = onItemDismissed,
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
