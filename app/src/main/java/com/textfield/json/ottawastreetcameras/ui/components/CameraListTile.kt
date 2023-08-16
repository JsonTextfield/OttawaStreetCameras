package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortMode
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraListTile(camera: Camera, onClick: (Camera) -> Unit, onLongClick: (Camera) -> Unit) {
    val cameraManager = CameraManager.getInstance()
    val cameraState by cameraManager.cameraState.collectAsState()
    val isSelected = cameraState.selectedCameras.contains(camera)
    Surface(
        modifier = Modifier
            .defaultMinSize(minHeight = 50.dp)
            .combinedClickable(
                onClick = { onClick(camera) },
                onLongClick = { onLongClick(camera) }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isSelected) {
                        colorResource(R.color.colorAccent)
                    }
                    else if (isSystemInDarkTheme()) {
                        Color.Black
                    }
                    else {
                        Color.Unspecified
                    }
                )
        ) {
            if (cameraState.sortMode == SortMode.DISTANCE && camera.distance > -1) {
                var distance = camera.distance.toDouble()
                val distanceString =
                    if (distance > 9000e3) {
                        ">9000\nkm"
                    }
                    else if (distance >= 100e3) {
                        "${(distance / 1000).roundToInt()}\nkm"
                    }
                    else if (distance >= 500) {
                        distance = (distance / 100.0).roundToInt().toDouble() / 10
                        "$distance\nkm"
                    }
                    else {
                        "${distance.roundToInt()}\nm"
                    }
                Text(
                    distanceString,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(5.dp)
                )
            }
            Column(
                modifier = Modifier
                    .padding(all = 10.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    camera.name,
                    color = if (isSelected) Color.White else Color.Unspecified
                )
                if (camera.neighbourhood.isNotBlank()) {
                    Text(
                        camera.neighbourhood,
                        color = if (isSelected) {
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
            val context = LocalContext.current
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    camera.isFavourite = !camera.isFavourite
                    cameraManager.favouriteCamera(context, camera)
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
                        tint = if (isSelected) {
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
    }
}