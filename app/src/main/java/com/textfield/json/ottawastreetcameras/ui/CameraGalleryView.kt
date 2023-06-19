package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.entities.Camera


@Composable
fun CameraGalleryView(cameras: List<Camera>, onItemClick: (Camera) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(cameras.size) {
            CameraGalleryTile(cameras[it], onItemClick)
        }
    }
}