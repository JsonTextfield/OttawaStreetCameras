package com.textfield.json.ottawastreetcameras.ui.viewmodels

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.textfield.json.ottawastreetcameras.entities.Camera
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

class CameraViewModel(
    var cameras: List<Camera> = emptyList(),
    var displayedCameras: List<Camera> = emptyList(),
    var isShuffling: Boolean = false,
) : ViewModel() {

    fun downloadImage(context: Context, camera: Camera, callback: (Camera) -> Unit = {}) {
        val request = ImageRequest(camera.url, { response ->
            if (response != null && saveImage(context, response, camera.name)) {
                callback(camera)
            }
        }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, {
            Log.w("STREETCAMS", it)
        })
        Volley.newRequestQueue(context).add(request)
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