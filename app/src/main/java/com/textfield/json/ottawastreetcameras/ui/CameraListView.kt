package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraListView(cameras: List<Camera>, onItemClick: (Camera) -> Unit, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        items(cameras.size) {
            CameraListTile(cameras[it]) {
                onItemClick(cameras[it])
            }
        }
        item {
            val context = LocalContext.current
            Text(
                context.resources.getQuantityString(R.plurals.camera_count, cameras.size, cameras.size),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
    }
}