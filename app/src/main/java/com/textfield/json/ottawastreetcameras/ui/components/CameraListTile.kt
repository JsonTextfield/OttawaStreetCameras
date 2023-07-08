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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraListTile(camera: Camera, onClick: () -> Unit) {
    val cameraManager = CameraManager.getInstance()
    val cameraState = cameraManager.cameraState.observeAsState()
    Surface(
        color = Color.Transparent, modifier = Modifier
            .defaultMinSize(minHeight = 50.dp)
            .combinedClickable(onClick = {
                if (cameraState.value?.selectedCameras?.isNotEmpty() == true) {
                    cameraManager.selectCamera(camera)
                }
                else {
                    onClick()
                }
            }, onLongClick = {
                cameraManager.selectCamera(camera)
            })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (cameraState.value?.selectedCameras?.contains(camera) == true) {
                        colorResource(R.color.colorAccent)
                    }
                    else if (isSystemInDarkTheme()) {
                        Color.Black
                    }
                    else {
                        Color.White
                    }
                )
        ) {

            Column(
                modifier = Modifier
                    .padding(all = 10.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    camera.name,
                )
                if (camera.neighbourhood.isNotBlank()) {
                    Text(
                        camera.neighbourhood,
                        color = if (isSystemInDarkTheme() || cameraState.value?.selectedCameras?.contains(camera) == true) Color.LightGray else Color.DarkGray,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                    )
                }
            }
            val context = LocalContext.current
            IconButton(modifier = Modifier.align(Alignment.CenterVertically), onClick = {
                camera.isFavourite = !camera.isFavourite
                cameraManager.favouriteCamera(context, camera)
            }) {
                if (cameraState.value?.favouriteCameras?.contains(camera) == true) {
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
                    )
                }
            }
        }
    }
}