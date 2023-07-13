package com.textfield.json.ottawastreetcameras.ui.activities

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsRequestQueue
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.components.AppTheme
import com.textfield.json.ottawastreetcameras.ui.components.BackButton
import com.textfield.json.ottawastreetcameras.ui.components.CameraActivityContent
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

class CameraActivity : AppCompatActivity() {
    private var cameras = ArrayList<Camera>()
    private var selectedCameras = ArrayList<Camera>()
    private val requestForSave = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val shuffle = intent.getBooleanExtra("shuffle", false)

        cameras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("cameras", Camera::class.java) ?: cameras
        }
        else {
            intent.getParcelableArrayListExtra("cameras") ?: cameras
        }

        loadView(shuffle)
    }

    private fun loadView(shuffle: Boolean) {
        setContent {
            AppTheme() {
                Scaffold() {
                    Box(modifier = Modifier.padding(it)) {
                        CameraActivityContent(cameras, shuffle)
                        BackButton()
                    }
                }
            }
        }
    }

    private fun requestStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestForSave
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PackageManager.PERMISSION_GRANTED in grantResults && requestCode == requestForSave) {
            saveSelectedImages()
        }
    }

    private fun saveSelectedImages() {
        var imagesSaved = 0
        for (camera in selectedCameras) {
            val request = ImageRequest(camera.url, { response ->
                if (response != null) {
                    saveImage(response, camera.name)
                    imagesSaved++
                }
            }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, {
                Log.w("STREETCAMS", it)
            })
            StreetCamsRequestQueue.getInstance(this).add(request)
        }

        Snackbar.make(
            window.decorView.rootView,
            resources.getQuantityString(R.plurals.images_saved, imagesSaved, imagesSaved),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun saveImage(bitmapImage: Bitmap, fileName: String): Boolean {
        // Add a media item that other apps shouldn't see until the item is fully written to the media store.
        val resolver = applicationContext.contentResolver

        // Find all image files on the primary external storage device.
        val imageCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, (fileName + "_" + Date().toString() + ".jpg").replace(" ", "_"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val songContentUri = resolver.insert(imageCollection, imageDetails)

        resolver.openFileDescriptor(songContentUri!!, "w", null).use { pfd ->
            // Write data into the pending file.

            try {
                val out = FileOutputStream(pfd?.fileDescriptor)
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: IOException) {
                Log.e("StreetCams", e.message ?: e.stackTraceToString())
                return false
            }
        }

        // Now that we're finished, release the "pending" status
        imageDetails.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        resolver.update(songContentUri, imageDetails, null, null)
        return true
    }
}