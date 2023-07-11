package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.delay


@Composable
fun CameraActivityContent(cameras: List<Camera>, shuffle: Boolean = false) {
    LazyColumn {
        if (shuffle) {
            item {
                var camera by remember { mutableStateOf(cameras.random()) }
                LaunchedEffect("shuffle") {
                    while (true) {
                        camera = cameras.random()
                        camera = cameras.random()
                        delay(6000)
                    }
                }
                CameraView(camera, true)
            }
        }
        else {
            items(cameras.size) {
                CameraView(cameras[it], false)
            }
        }
    }
}