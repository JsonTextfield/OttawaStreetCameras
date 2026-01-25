package com.jsontextfield.shared.ui.main

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jsontextfield.shared.entities.BilingualObject
import com.jsontextfield.shared.entities.Camera
import com.jsontextfield.shared.ui.theme.cameraNameBackground
import com.jsontextfield.shared.ui.theme.favourite
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import streetcams.shared.generated.resources.Res
import streetcams.shared.generated.resources.round_check_24
import streetcams.shared.generated.resources.round_star_24

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
            val context = LocalPlatformContext.current
            AsyncImage(
                model = ImageRequest
                    .Builder(context)
                    .data(camera.preview)
                    .crossfade(500)
                    .build(),
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
                        painterResource(Res.drawable.round_star_24),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.TopEnd),
                        tint = favourite,
                    )
                }
                if (camera.isSelected) {
                    Icon(
                        painterResource(Res.drawable.round_check_24),
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