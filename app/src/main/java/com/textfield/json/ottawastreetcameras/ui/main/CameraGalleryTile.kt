package com.textfield.json.ottawastreetcameras.ui.main

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
import androidx.compose.ui.res.painterResource
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
import com.textfield.json.ottawastreetcameras.ui.theme.favouriteColour

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
                ),
        ) {
            val context = LocalContext.current
            var model by remember {
                mutableStateOf(
                    ImageRequest
                        .Builder(context)
                        .data(camera.preview)
                        .crossfade(500)
                        .build()
                )
            }
            LaunchedEffect(camera.name) {
                model = ImageRequest
                    .Builder(context)
                    .data(camera.preview)
                    .crossfade(500)
                    .build()
            }
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface)
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(color = Color.Black.copy(alpha = .6f))
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    camera.name,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (camera.isFavourite) {
                Icon(
                    painterResource(R.drawable.round_star_24),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.TopEnd),
                    tint = favouriteColour,
                )
            }
            if (camera.isSelected) {
                Icon(
                    painterResource(R.drawable.round_check_24),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        )
                        .align(Alignment.TopStart),
                    tint = MaterialTheme.colorScheme.onPrimary,
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