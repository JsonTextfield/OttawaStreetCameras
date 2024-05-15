package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortMode
import com.textfield.json.ottawastreetcameras.entities.Camera

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraListTile(
    cameraViewModel: CameraViewModel,
    camera: Camera,
    onClick: (Camera) -> Unit = {},
    onLongClick: (Camera) -> Unit = {},
) {
    val cameraState by cameraViewModel.cameraState.collectAsState()
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
                colorResource(R.color.colorAccent)
            }
            else if (isSystemInDarkTheme()) {
                Color.Black
            }
            else {
                Color.White
            }
        ),
        headlineContent = {
            Text(
                camera.name,
                color = if (camera.isSelected) Color.White else Color.Unspecified
            )
        },
        supportingContent = if (camera.neighbourhood.isNotBlank()) {
            {
                Text(
                    camera.neighbourhood,
                    color = if (camera.isSelected) {
                        Color.White
                    }
                    else if (isSystemInDarkTheme()) {
                        Color.LightGray
                    }
                    else {
                        Color.DarkGray
                    },
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                )
            }
        }
        else null,
        leadingContent = if (cameraState.sortMode == SortMode.DISTANCE && camera.distance > -1) {
            {
                Text(
                    camera.distanceString,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(5.dp),
                    color = if (camera.isSelected) Color.White else Color.Unspecified
                )
            }
        }
        else null,
        trailingContent = {
            IconButton(
                onClick = {
                    cameraViewModel.favouriteCameras(listOf(camera))
                }
            ) {
                if (camera.isFavourite) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = stringResource(R.string.remove_from_favourites),
                        tint = colorResource(id = R.color.favouriteColour)
                    )
                }
                else {
                    Icon(
                        Icons.Rounded.StarBorder,
                        contentDescription = stringResource(R.string.add_to_favourites),
                        tint = if (camera.isSelected) {
                            Color.White
                        }
                        else if (isSystemInDarkTheme()) {
                            Color.White
                        }
                        else {
                            Color.DarkGray
                        },
                    )
                }
            }
        }
    )
}