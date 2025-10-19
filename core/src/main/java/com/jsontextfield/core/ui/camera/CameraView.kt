package com.jsontextfield.core.ui.camera

import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.jsontextfield.core.entities.Camera
import com.jsontextfield.core.ui.theme.cameraNameBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraView(
    camera: Camera,
    update: Boolean = false,
    onLongClick: (Camera) -> Unit = {},
) {
    var drawable by remember { mutableStateOf<Drawable?>(null) }
    val context = LocalContext.current
    LaunchedEffect(update) {
        val request = ImageRequest.Builder(context)
            .data(camera.url)
            .build()
        drawable = context.imageLoader.execute(request).drawable
    }
    Box(
        modifier = Modifier
            .heightIn(
                0.dp,
                (LocalWindowInfo.current.containerSize.height / LocalDensity.current.density).dp
            )
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongClick(camera) },
            )
    ) {
        drawable?.let {
            Image(
                bitmap = it.toBitmap().asImageBitmap(),
                contentDescription = camera.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(radius = 10.dp),
                contentScale = ContentScale.FillWidth,
            )
            Image(
                bitmap = it.toBitmap().asImageBitmap(),
                contentDescription = camera.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.matchParentSize(),
            )

            Text(
                text = camera.name,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(4.dp)
                    .background(
                        color = cameraNameBackground,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(
                        vertical = 4.dp,
                        horizontal = 12.dp,
                    ),
            )
        }
    }
}