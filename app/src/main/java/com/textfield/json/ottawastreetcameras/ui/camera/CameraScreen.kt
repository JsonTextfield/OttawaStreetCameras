package com.textfield.json.ottawastreetcameras.ui.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.network.CameraDownloadService
import com.textfield.json.ottawastreetcameras.ui.components.BackButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
    cameras: List<Camera> = emptyList(),
    displayedCameras: List<Camera> = emptyList(),
    isShuffling: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) {
        Box(modifier = Modifier.padding(it)) {
            var update by remember { mutableStateOf(false) }
            LaunchedEffect(update) {
                if (!isShuffling) {
                    delay(6000)
                    update = !update
                }
            }
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            CameraViewList(
                cameras = cameras,
                displayedCameras = displayedCameras,
                shuffle = isShuffling,
                update = update,
                onItemLongClick = { camera ->
                    scope.launch {
                        CameraDownloadService.saveImage(context, camera)
                        snackbarHostState.showSnackbar(
                            context.resources.getString(
                                R.string.image_saved,
                                camera.name,
                            )
                        )
                    }
                }
            )
            BackButton()
        }
    }
}