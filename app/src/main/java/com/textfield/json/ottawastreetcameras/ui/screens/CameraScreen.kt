package com.textfield.json.ottawastreetcameras.ui.screens

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
import com.textfield.json.ottawastreetcameras.ui.components.BackButton
import com.textfield.json.ottawastreetcameras.ui.components.CameraActivityContent
import com.textfield.json.ottawastreetcameras.ui.viewmodels.CameraViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(cameraViewModel: CameraViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
        Box(modifier = Modifier.padding(it)) {
            var update by remember { mutableStateOf(false) }
            LaunchedEffect(update) {
                if (!cameraViewModel.isShuffling) {
                    delay(6000)
                    update = !update
                }
            }
            CameraActivityContent(
                cameras = cameraViewModel.cameras,
                displayedCameras = cameraViewModel.displayedCameras,
                shuffle = cameraViewModel.isShuffling,
                update = update,
                onItemLongClick = { camera ->
                    cameraViewModel.downloadImage(context, camera) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                context.resources.getString(
                                    R.string.image_saved,
                                    camera.name
                                )
                            )
                        }
                    }
                }
            )
            BackButton()
        }
    }
}