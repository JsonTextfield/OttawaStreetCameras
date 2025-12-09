package com.textfield.json.ottawastreetcameras.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import coil.ImageLoader
import coil.request.ImageRequest
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.main.MainViewModel
import kotlinx.coroutines.delay
import org.koin.java.KoinJavaComponent.inject

class MyAppWidget : GlanceAppWidget() {
    val mainViewModel: MainViewModel by inject(MainViewModel::class.java)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            val uiState by mainViewModel.uiState.collectAsState()
            val context = LocalContext.current
            var bitmap by remember { mutableStateOf<Bitmap?>(null) }
            var camera by remember { mutableStateOf(uiState.allCameras.random()) }
            LaunchedEffect(Unit) {
                while (true) {
                    bitmap = getImageBitmap(context, camera)
                    delay(6000)
                    camera = uiState.allCameras.random()
                }
            }
            GlanceTheme {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = GlanceModifier
                        .padding(0.dp)
                        .background(GlanceTheme.colors.background)
                        .appWidgetBackground()
                ) {
                    bitmap?.let {
                        Image(
                            provider = ImageProvider(it),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = GlanceModifier.fillMaxSize()
                        )
                    }
                    Text(
                        text = camera.name,
                        modifier = GlanceModifier.fillMaxWidth()
                            .background(Color.Black.copy(alpha = .2f))
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        maxLines = 2,
                        style = TextDefaults.defaultTextStyle.copy(
                            color = ColorProvider(Color.White),
                            textAlign = TextAlign.Center,
                        )
                    )
                }
            }
        }
    }

    suspend fun getImageBitmap(context: Context, camera: Camera): Bitmap? {
        return ImageLoader(context).newBuilder().respectCacheHeaders(false).build().execute(
            ImageRequest.Builder(context).data(camera.url).build()
        ).drawable?.toBitmapOrNull()
    }
}