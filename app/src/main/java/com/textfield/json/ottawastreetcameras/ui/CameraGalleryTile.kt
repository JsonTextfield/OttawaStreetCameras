package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.textfield.json.ottawastreetcameras.entities.Camera

@Composable
fun CameraGalleryTile(camera: Camera, onClick: (Camera) -> Unit) {
    Box(
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(10.dp)
            )
            .aspectRatio(1f),
    ) {
        Surface(onClick = { onClick(camera) }) {
            AsyncImage(
                model = camera.url,
                contentDescription = camera.getName(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.DarkGray),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        color = Color(0x66000000),
                    )
                    .padding(2.dp),
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
        }
    }
}