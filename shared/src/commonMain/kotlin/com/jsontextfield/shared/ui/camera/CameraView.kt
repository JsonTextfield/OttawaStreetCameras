package com.jsontextfield.shared.ui.camera

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.jsontextfield.shared.ui.theme.cameraNameBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraView(
    title: String,
    url: String,
    update: Boolean,
    onLongClick: () -> Unit,
) {
    val painter = rememberAsyncImagePainter(url)
    LaunchedEffect(update) {
        painter.restart()
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
        Image(
            painter = painter,
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .blur(radius = 10.dp),
            contentScale = ContentScale.FillWidth,
        )
        Image(
            painter = painter,
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