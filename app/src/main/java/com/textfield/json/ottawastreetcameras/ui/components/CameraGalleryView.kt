package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera


@Composable
fun CameraGalleryView(
    cameraViewModel: CameraViewModel,
    gridState: LazyGridState,
    onItemClick: (Camera) -> Unit,
    onItemLongClick: (Camera) -> Unit,
) {
    val cameraState by cameraViewModel.cameraState.collectAsState()
    val cameras = cameraState.displayedCameras
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Adaptive(100.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(cameras.size) {
            CameraGalleryTile(cameraViewModel, cameras[it], onItemClick, onItemLongClick)
        }

        item {
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(10.dp))
                    .aspectRatio(1f)
            ) {
                Text(
                    pluralStringResource(R.plurals.camera_count, cameras.size, cameras.size).replace(" ", "\n"),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}