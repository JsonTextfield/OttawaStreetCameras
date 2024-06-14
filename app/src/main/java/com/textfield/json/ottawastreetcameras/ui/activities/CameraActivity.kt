package com.textfield.json.ottawastreetcameras.ui.activities

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.textfield.json.ottawastreetcameras.CameraDownloadService
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.components.BackButton
import com.textfield.json.ottawastreetcameras.ui.components.CameraActivityContent
import com.textfield.json.ottawastreetcameras.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isShuffling = intent.getBooleanExtra("shuffle", false)

        var cameras = ArrayList<Camera>()
        var displayedCameras = ArrayList<Camera>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameras = intent.getParcelableArrayListExtra("cameras", Camera::class.java) ?: cameras
            displayedCameras =
                intent.getParcelableArrayListExtra("displayedCameras", Camera::class.java) ?: displayedCameras
        }
        else {
            cameras = intent.getParcelableArrayListExtra("cameras") ?: cameras
            displayedCameras = intent.getParcelableArrayListExtra("displayedCameras") ?: displayedCameras
        }

        enableEdgeToEdge()
        setContent {
            AppTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        var update by remember { mutableStateOf(false) }
                        LaunchedEffect(update) {
                            if (!isShuffling) {
                                delay(6000)
                                update = !update
                            }
                        }
                        val scope = rememberCoroutineScope()
                        val context = LocalContext.current
                        CameraActivityContent(
                            cameras = cameras,
                            displayedCameras = displayedCameras,
                            shuffle = isShuffling,
                            update = update,
                            onItemLongClick = { camera ->
                                CameraDownloadService.downloadImage(
                                    camera.url,
                                    onComplete = { bitmap ->
                                        if (saveImage(context, bitmap, camera.name)) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    resources.getString(
                                                        R.string.image_saved,
                                                        camera.name,
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    onError = {},
                                )
                            }
                        )
                        BackButton()
                    }
                }
            }
        }
    }

    private fun saveImage(context: Context, bitmapImage: Bitmap, fileName: String): Boolean {
        // Add a media item that other apps shouldn't see until the item is fully written to the media store.
        val resolver = context.contentResolver

        // Find all image files on the primary external storage device.
        val imageCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, ("${fileName}_${Date()}.jpg").replace(" ", "_"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imgUri = resolver.insert(imageCollection, imageDetails)
        if (imgUri != null) {
            try {
                resolver.openFileDescriptor(imgUri, "w", null)?.use { pfd ->
                    FileOutputStream(pfd.fileDescriptor).use { out ->
                        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
                }
            } catch (e: IOException) {
                Log.e("StreetCams", e.message ?: e.stackTraceToString())
                return false
            }

            // Now that we're finished, release the "pending" status
            imageDetails.clear()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            resolver.update(imgUri, imageDetails, null, null)
            return true
        }
        return false
    }
}