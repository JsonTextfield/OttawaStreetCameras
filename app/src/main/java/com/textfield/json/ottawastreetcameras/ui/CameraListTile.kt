package com.textfield.json.ottawastreetcameras.ui

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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera


@Composable
fun CameraListTile(camera: Camera, onClick: () -> Unit) {
    Surface(color = Color.Transparent, onClick = onClick, modifier = Modifier.defaultMinSize(minHeight = 50.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {

            Column(
                modifier = Modifier
                    .padding(all = 10.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    camera.getName(),
                )
                if (camera.neighbourhood.isNotBlank()) {
                    Text(
                        camera.neighbourhood,
                        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                    )
                }
            }
            val context = LocalContext.current
            IconButton(modifier = Modifier.align(Alignment.CenterVertically), onClick = {
                camera.setFavourite(!camera.isFavourite)
                CameraManager.getInstance().favouriteCamera(context, camera)
            }, content = {
                val context = LocalContext.current
                Icon(
                    if (camera.isFavourite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    context.getString(if (camera.isFavourite) R.string.remove_from_favourites else R.string.remove_from_favourites),
                    tint = if (camera.isFavourite) Color.Yellow else LocalContentColor.current
                )
            })
        }
    }
}