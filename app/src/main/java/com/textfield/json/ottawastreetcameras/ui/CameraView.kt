package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.textfield.json.ottawastreetcameras.entities.Camera


@Composable
fun CameraView(camera: Camera) {
    Box(
        modifier = Modifier
            .heightIn(0.dp, LocalConfiguration.current.screenHeightDp.dp - 30.dp)
            .widthIn(0.dp, LocalConfiguration.current.screenWidthDp.dp)
            .wrapContentHeight()
    ) {

        /*LaunchedEffect(Unit) {
            while (true) {
                Log.e("UPDATE", model)
                model = camera.url
                imageRequest = ImageRequest.Builder(context).data(model).crossfade(300).build()
                delay(3000)
            }
        }*/
        AsyncImage(
            model = camera.url,
            contentDescription = camera.getName(),
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Fit,
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(
                    color = Color(0x55000000),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(5.dp),
        ) {

            Text(
                camera.getName(),
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    }
}