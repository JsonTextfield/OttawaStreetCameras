package com.textfield.json.ottawastreetcameras.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.snackbar.Snackbar
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ViewMode
import com.textfield.json.ottawastreetcameras.entities.Camera

@Composable
fun MainContent(cameraViewModel: CameraViewModel, listState: LazyListState, gridState: LazyGridState) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            val cameraState by cameraViewModel.cameraState.collectAsState()
            val context = LocalContext.current
            val onItemClick = { camera: Camera ->
                if (cameraState.selectedCameras.isNotEmpty()) {
                    cameraViewModel.selectCamera(camera)
                }
                else {
                    cameraViewModel.showCameras(
                        context = context,
                        cameras = arrayListOf(camera),
                        displayedCameras = cameraState.displayedCameras,
                    )
                }
            }
            val onItemLongClick = { camera: Camera -> cameraViewModel.selectCamera(camera) }
            val onItemDismissed = { camera: Camera ->
                cameraViewModel.hideCameras(listOf(camera))
                val visibilityStringId = if (camera.isVisible) R.string.unhidden else R.string.hidden
                Snackbar.make(
                    (context as Activity).window.decorView.rootView,
                    context.getString(
                        R.string.camera_visibility_changed,
                        camera.name,
                        context.getString(visibilityStringId)
                    ),
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.undo) {
                    cameraViewModel.hideCameras(listOf(camera))
                }.show()
            }
            when (cameraState.viewMode) {
                ViewMode.LIST -> {
                    CameraListView(
                        cameraViewModel,
                        listState = listState,
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick,
                        onItemDismissed = onItemDismissed,
                    )
                }

                ViewMode.MAP -> {
                    CameraMapView(
                        cameraViewModel,
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick
                    )
                }

                ViewMode.GALLERY -> {
                    CameraGalleryView(
                        cameraViewModel,
                        gridState = gridState,
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick
                    )
                }
            }
        }
    }
}
