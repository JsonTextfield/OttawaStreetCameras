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
import com.jsontextfield.core.ui.theme.cameraNameBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraView(
    title: String,
    url: String,
    update: Boolean,
    onLongClick: () -> Unit,
) {
    var drawable by remember { mutableStateOf<Drawable?>(null) }
    val context = LocalContext.current
    LaunchedEffect(update) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        drawable = context.imageLoader.execute(request).drawable
    }
    Box(
        modifier = Modifier
            .heightIn(
                max = (LocalWindowInfo.current.containerSize.height / LocalDensity.current.density).dp
            )
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick,
            )
    ) {
        drawable?.let {
            Image(
                bitmap = it.toBitmap().asImageBitmap(),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(radius = 10.dp),
                contentScale = ContentScale.FillWidth,
            )
            Image(
                bitmap = it.toBitmap().asImageBitmap(),
                contentDescription = title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.matchParentSize(),
            )

            Text(
                text = title,
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