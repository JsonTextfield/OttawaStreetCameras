package com.jsontextfield.core.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jsontextfield.core.entities.BilingualObject
import com.jsontextfield.core.entities.Camera
import com.jsontextfield.core.ui.theme.cameraNameBackground
import com.jsontextfield.core.ui.theme.favourite

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraGalleryTile(
    camera: Camera = Camera(),
    onClick: (Camera) -> Unit = {},
    onLongClick: (Camera) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .semantics(true) {},
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        Box(
            modifier = Modifier
                .combinedClickable(
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
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(color = cameraNameBackground)
                    .padding(2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = camera.name,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                if (camera.isFavourite) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.TopEnd),
                        tint = favourite,
                    )
                }
                if (camera.isSelected) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                            ),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
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