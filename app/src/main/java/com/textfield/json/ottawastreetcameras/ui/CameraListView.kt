package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.textfield.json.ottawastreetcameras.entities.Camera

@Composable
fun CameraListView(cameras: List<Camera>, onItemClick: (Camera) -> Unit) {
    LazyColumn {
        items(cameras.size) {
            CameraListTile(cameras[it]) {
                onItemClick(cameras[it])
            }
        }
    }
}