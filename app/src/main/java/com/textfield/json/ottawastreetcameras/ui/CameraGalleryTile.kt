package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

@Composable
fun CameraGalleryTile(camera: Camera, onClick: (Camera) -> Unit) {
    CameraGalleryTileContent(camera = camera, onClick = onClick)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CameraGalleryTileContent(camera: Camera, onClick: (Camera) -> Unit) {
    val cameraManager = CameraManager.getInstance()
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .aspectRatio(1f),
    ) {
        Surface(
            modifier = Modifier.combinedClickable(
                onClick = {
                    if (cameraManager.getSelectedCameras().isNotEmpty()) {
                        cameraManager.selectCamera(camera)
                    } else {
                        onClick(camera)
                    }
                },
                onLongClick = { cameraManager.selectCamera(camera) },
            )
        ) {
            AsyncImage(
                model = camera.url,
                contentDescription = camera.getName(),
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(Color.DarkGray),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color =
                        if (cameraManager.isCameraSelected(camera)
                        ) {
                            colorResource(id = R.color.galleryTileSelectedColour)
                        } else {
                            Color.Transparent
                        }
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.cameraNameBackground))
                    .padding(horizontal = 5.dp, vertical = 2.dp),
            ) {

                Text(
                    camera.getName(),
                    modifier = Modifier.align(Alignment.BottomCenter),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 10.sp
                )
            }
            if (camera.isFavourite) {
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.TopEnd),
                    tint = Color.Yellow,
                )
            }
        }
    }
}