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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.network.CameraDownloadService
import com.textfield.json.ottawastreetcameras.ui.components.BackButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
    ids: String = "",
    isShuffling: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    cameraViewModel: CameraViewModel = viewModel<CameraViewModel>(),
) {
    val cameraList by cameraViewModel.cameraList.collectAsStateWithLifecycle()
    val allCameras by cameraViewModel.allCameras.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (allCameras.isEmpty()) {
            cameraViewModel.getAllCameras()
        }
    }
    LaunchedEffect(allCameras) {
        if (allCameras.isNotEmpty() && cameraList.isEmpty()) {
            if (isShuffling) {
                cameraViewModel.getRandomCamera()
            } else {
                cameraViewModel.getCameras(ids)
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) {
        Box(modifier = Modifier.padding(it)) {
            var update by remember { mutableStateOf(false) }
            LaunchedEffect(update) {
                delay(6000)
                if (isShuffling) {
                    cameraViewModel.getRandomCamera()
                }
                update = !update
            }
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            CameraViewList(
                cameras = cameraList,
                displayedCameras = allCameras,
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