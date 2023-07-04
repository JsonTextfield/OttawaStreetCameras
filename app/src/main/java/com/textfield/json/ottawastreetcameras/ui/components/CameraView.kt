package com.textfield.json.ottawastreetcameras.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.delay


@Composable
fun CameraView(camera: Camera, shuffle: Boolean = false) {
    Box(
        modifier = Modifier.heightIn(0.dp, LocalConfiguration.current.screenHeightDp.dp)
    ) {
        val context = LocalContext.current
        var drawable by remember { mutableStateOf<Drawable?>(null) }
        var oldDrawable by remember { mutableStateOf<Drawable?>(drawable) }
        val painter = rememberAsyncImagePainter(oldDrawable, contentScale = ContentScale.Fit)
        val backgroundPainter = rememberAsyncImagePainter(oldDrawable, contentScale = ContentScale.FillWidth)
        var imageRequest by remember {
            mutableStateOf(
                ImageRequest
                    .Builder(context)
                    .data(camera.url)
                    .listener { request, result ->
                        drawable = result.drawable
                    }
                    .build()
            )
        }

        if (!shuffle) {
            LaunchedEffect(Unit) {
                while (true) {
                    imageRequest = imageRequest
                        .newBuilder(context)
                        .data(camera.url)
                        .listener { request, result ->
                            drawable = result.drawable
                        }
                        .build()
                    delay(3000)
                }
            }
        }
        AsyncImage(
            model = imageRequest,
            contentDescription = camera.name,
            modifier = Modifier
                .fillMaxWidth()
                .blur(radius = 10.dp),
            contentScale = ContentScale.FillWidth,
            placeholder = backgroundPainter,
        )
        AsyncImage(
            model = imageRequest,
            contentDescription = camera.name,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Fit,
            placeholder = painter,
            onLoading = {
                oldDrawable = drawable
            }
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
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
}