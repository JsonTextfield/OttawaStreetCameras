package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.textfield.json.ottawastreetcameras.entities.Camera


@Composable
fun CameraActivityContent(cameras: List<Camera>, shuffle: Boolean = false) {
    LazyColumn {
        if (shuffle) {
            item {
                CameraView(cameras.random(), true)
            }
        } else {
            items(cameras.size) {
                CameraView(cameras[it], false)
            }
        }
    }
}