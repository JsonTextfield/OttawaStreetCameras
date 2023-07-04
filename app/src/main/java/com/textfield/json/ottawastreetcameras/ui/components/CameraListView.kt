package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

@Composable
fun CameraListView(cameras: List<Camera>, modifier: Modifier, listState: LazyListState, onItemClick: (Camera) -> Unit) {
    LazyColumn(modifier = modifier, state = listState) {
        items(cameras.size) {
            CameraListTile(cameras[it]) {
                onItemClick(cameras[it])
            }
        }
        item {
            Text(
                pluralStringResource(R.plurals.camera_count, cameras.size, cameras.size),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
    }
}