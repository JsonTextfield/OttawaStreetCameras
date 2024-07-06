package com.textfield.json.ottawastreetcameras.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.network.CameraDownloadServiceImpl

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraView(
    camera: Camera,
    shuffle: Boolean = false,
    update: Boolean = false,
    onLongClick: (Camera) -> Unit = {},
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(if (shuffle) camera.name else update) {
        CameraDownloadServiceImpl.downloadImage(
            camera.url,
            onComplete = { bitmap = it },
            onError = { bitmap = null },
        )
    }
    Box(
        modifier = Modifier
            .heightIn(0.dp, LocalConfiguration.current.screenHeightDp.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongClick(camera) }
            )
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = camera.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(radius = 10.dp),
                contentScale = ContentScale.FillWidth,
            )
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = camera.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.matchParentSize()
            )
            CameraLabel(
                camera = camera,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
fun CameraLabel(camera: Camera, modifier: Modifier) {
    Box(
        modifier = modifier
            .padding(horizontal = 5.dp, vertical = 2.dp)
            .background(
                color = colorResource(id = R.color.cameraNameBackground),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(
                vertical = 5.dp,
                horizontal = 10.dp,
            ),
    ) {
        Text(
            camera.name,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}