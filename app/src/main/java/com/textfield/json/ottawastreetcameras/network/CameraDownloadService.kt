package com.textfield.json.ottawastreetcameras.network

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.textfield.json.ottawastreetcameras.entities.Camera
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

object CameraDownloadService {
    suspend fun saveImage(context: Context, camera: Camera) {
        val request = ImageRequest.Builder(context)
            .data(camera.url)
            .build()
        val response = context.imageLoader.execute(request)
        response.drawable?.let {
            val bitmapImage = it.toBitmap()

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
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    ("${camera.name}_${Date()}.jpg").replace(" ", "_")
                )
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
                    Log.e("CameraDownloadService", e.stackTraceToString())
                }

                // Now that we're finished, release the "pending" status
                imageDetails.clear()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                }
                resolver.update(imgUri, imageDetails, null, null)
            }
        }
    }
}