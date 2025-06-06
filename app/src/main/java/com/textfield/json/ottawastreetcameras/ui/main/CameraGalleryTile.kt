package com.textfield.json.ottawastreetcameras.ui.main

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraGalleryTile(
    camera: Camera = Camera(),
    onClick: (Camera) -> Unit = {},
    onLongClick: (Camera) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .aspectRatio(1f).semantics(true){},
    ) {
        Surface(
            modifier = Modifier.combinedClickable(
                onClick = { onClick(camera) },
                onLongClick = { onLongClick(camera) },
            )
        ) {
            val context = LocalContext.current
            var model by remember {
                mutableStateOf(
                    ImageRequest.Builder(context).data(camera.preview).crossfade(500).build()
                )
            }
            LaunchedEffect(camera.name) {
                model = ImageRequest.Builder(context).data(camera.preview).crossfade(500).build()
            }
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (camera.isSelected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
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
                    camera.name,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (camera.isFavourite) {
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.TopEnd),
                    tint = colorResource(id = R.color.favouriteColour),
                )
            }
        }
    }
}

@Preview
@Composable
private fun CameraGalleryTilePreview() {
    CameraGalleryTile(
        Camera(
            _name = BilingualObject("Name", "Name"),
            _neighbourhood = BilingualObject("Neighbourhood", "Neighbourhood"),
        )
    )
}