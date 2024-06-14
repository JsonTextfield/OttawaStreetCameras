package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.viewmodels.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.viewmodels.ViewMode
import kotlinx.coroutines.launch

@Composable
fun MainContent(
    mainViewModel: MainViewModel,
    listState: LazyListState,
    gridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
) {
    val cameraState by mainViewModel.cameraState.collectAsState()
    val context = LocalContext.current
    val onItemClick = { camera: Camera ->
        if (cameraState.selectedCameras.isNotEmpty()) {
            mainViewModel.selectCamera(camera)
        }
        else {
            mainViewModel.showCameras(
                context = context,
                cameras = arrayListOf(camera),
                displayedCameras = cameraState.displayedCameras,
            )
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
            CameraListView(
                mainViewModel,
                listState = listState,
                onItemClick = onItemClick,
                onItemLongClick = onItemLongClick,
                onItemDismissed = onItemDismissed,
            )
        }

        ViewMode.MAP -> {
            CameraMapView(
                mainViewModel,
                onItemClick = onItemClick,
                onItemLongClick = onItemLongClick
            )
        }

        ViewMode.GALLERY -> {
            CameraGalleryView(
                mainViewModel,
                gridState = gridState,
                onItemClick = onItemClick,
                onItemLongClick = onItemLongClick
            )
        }
    }
}
