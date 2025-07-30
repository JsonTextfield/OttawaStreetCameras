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
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

@Composable
fun MainContent(
    cameraState: CameraState,
    listState: LazyListState,
    gridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
    searchText: String = "",
    onHideCameras: (List<Camera>) -> Unit = {},
    onFavouriteCameras: (List<Camera>) -> Unit = {},
    onCameraLongClick: (Camera) -> Unit = {},
    onCameraClicked: (Camera) -> Unit = {},
) {
    val context = LocalContext.current
    val onItemLongClick = onCameraLongClick
    var dismissedCamera by remember { mutableStateOf<Camera?>(null) }
    LaunchedEffect(dismissedCamera) {
        dismissedCamera?.let {
            onHideCameras(listOf(it))
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
                onHideCameras(listOf(it))
            }
            dismissedCamera = null
        }
    }
    Column {
        when (cameraState.viewMode) {
            ViewMode.LIST -> {
                CameraListItemList(
                    searchText = searchText,
                    cameraState = cameraState,
                    listState = listState,
                    onItemClick = onCameraClicked,
                    onItemLongClick = onItemLongClick,
                    onItemDismissed = { dismissedCamera = it },
                    onFavouriteClick = { onFavouriteCameras(listOf(it)) },
                )
            }

            ViewMode.MAP -> {
                CameraMapView(
                    searchText = searchText,
                    cameraState = cameraState,
                    onItemClick = onCameraClicked,
                    onItemLongClick = onItemLongClick,
                )
            }

            ViewMode.GALLERY -> {
                CameraGalleryView(
                    searchText = searchText,
                    cameraState = cameraState,
                    gridState = gridState,
                    onItemClick = onCameraClicked,
                    onItemLongClick = onItemLongClick,
                )
            }
        }
    }
}
