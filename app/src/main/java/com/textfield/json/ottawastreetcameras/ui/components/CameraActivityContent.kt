package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.delay

@Composable
fun CameraActivityContent(
    cameras: List<Camera>,
    shuffle: Boolean = false,
    update: Boolean,
    onItemLongClick: (Camera) -> Unit,
) {
    val verticalScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(verticalScrollState)
            .fillMaxSize()
    ) {
        if (shuffle) {
            var camera by remember { mutableStateOf(cameras.random()) }
            CameraView(camera, true) { onItemLongClick(it) }
            LaunchedEffect(camera) {
                delay(6000)
                camera = cameras.random()
            }
        }
        else {
            cameras.map { camera ->
                CameraView(camera, update = update) { onItemLongClick(camera) }
            }
        }
    }
}