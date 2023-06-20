package com.textfield.json.ottawastreetcameras.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.delay


@Composable
fun CameraView(camera: Camera, shuffle: Boolean = false) {
    Box(
        modifier = Modifier
            .heightIn(0.dp, LocalConfiguration.current.screenHeightDp.dp)
            .widthIn(0.dp, LocalConfiguration.current.screenWidthDp.dp)
            .wrapContentHeight()
    ) {
        var model by remember {
            mutableStateOf(camera.url)
        }
        if (!shuffle) {
            LaunchedEffect(Unit) {
                while (true) {
                    Log.e("UPDATE", model)
                    model = camera.url
                    delay(3000L)
                }
            }
        }
        val context = LocalContext.current
        val imageRequest = ImageRequest.Builder(context).data(model).build()
        AsyncImage(
            model = imageRequest,
            contentDescription = camera.getName(),
            modifier = Modifier
                .fillMaxWidth()
                .blur(radius = 10.dp),
            contentScale = ContentScale.FillWidth,
        )
        AsyncImage(
            model = imageRequest,
            contentDescription = camera.getName(),
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Fit,
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 5.dp, vertical = 2.dp)
                .background(
                    color = CameraNameBackground,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(
                    vertical = 5.dp,
                    horizontal = 10.dp,
                ),
        ) {

            Text(
                camera.getName(),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}