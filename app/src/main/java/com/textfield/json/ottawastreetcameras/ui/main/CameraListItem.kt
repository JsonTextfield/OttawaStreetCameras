package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraListItem(
    camera: Camera,
    showDistance: Boolean = false,
    onClick: (Camera) -> Unit = {},
    onLongClick: (Camera) -> Unit = {},
    onFavouriteClick: (Camera) -> Unit = {},
) {
    ListItem(
        modifier = Modifier
            .defaultMinSize(minHeight = 50.dp)
            .combinedClickable(
                onClick = { onClick(camera) },
                onLongClick = { onLongClick(camera) }
            )
            .fillMaxWidth(),
        colors = ListItemDefaults.colors(
            containerColor = if (camera.isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                ListItemDefaults.containerColor
            }
        ),
        headlineContent = {
            Text(camera.name)
        },
        supportingContent = {
            if (camera.neighbourhood.isNotBlank()) {
                Text(camera.neighbourhood)
            }
        },
        leadingContent = if (showDistance) {
            {
                Text(
                    camera.distanceString,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(5.dp),
                )
            }
        } else null,
        trailingContent = {
            IconButton(
                onClick = { onFavouriteClick(camera) }
            ) {
                if (camera.isFavourite) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = stringResource(R.string.remove_from_favourites),
                        tint = colorResource(id = R.color.favouriteColour)
                    )
                } else {
                    Icon(
                        Icons.Rounded.StarBorder,
                        contentDescription = stringResource(R.string.add_to_favourites),
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun CameraListItemPreview() {
    CameraListItem(
        Camera(
            _name = BilingualObject("Name", "Name"),
            _neighbourhood = BilingualObject("Neighbourhood", "Neighbourhood"),
        )
    )
}