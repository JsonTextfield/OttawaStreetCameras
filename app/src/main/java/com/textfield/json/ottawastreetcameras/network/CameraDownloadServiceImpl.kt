package com.textfield.json.ottawastreetcameras.network

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortByName
import com.textfield.json.ottawastreetcameras.entities.Camera
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

object CameraDownloadServiceImpl : CameraDownloadService {
    private const val TAG = "DownloadService"
    private val client = OkHttpClient()

    override fun download(
        context: Context,
        onError: () -> Unit,
        onComplete: (cameras: List<Camera>) -> Unit,
    ) {
        Log.d(TAG, "downloading cameras")
        val url = "https://nacudfxzbqaesoyjfluh.supabase.co/rest/v1/cameras?select=*&city=eq.ottawa"
        val request = Request.Builder()
            .url(url)
            .header("apiKey", context.getString(R.string.supabase_key))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, e.toString())
                onError()
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonArray = JSONArray(response.body?.string())
                val cameras = (0 until jsonArray.length())
                    .map { Camera.fromJson(jsonArray.getJSONObject(it)) }
                    .sortedWith(SortByName)
                onComplete(cameras)
            }
        })
    }

    fun downloadImage(
        url: String,
        onError: () -> Unit = {},
        onComplete: (bitmap: Bitmap) -> Unit = {},
    ) {
        Log.d(TAG, "downloading image")
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, e.toString())
                onError()
            }

            override fun onResponse(call: Call, response: Response) {
                val inputStream = response.body?.byteStream()
                onComplete(BitmapFactory.decodeStream(inputStream))
            }
        })
    }

    fun saveImage(
        context: Context,
        camera: Camera,
        onError: () -> Unit = {},
        onComplete: () -> Unit = {},
    ) {
        downloadImage(
            camera.url,
            onComplete = { bitmapImage ->
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
                    put(MediaStore.Images.Media.DISPLAY_NAME, ("${camera.name}_${Date()}.jpg").replace(" ", "_"))
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
                    } catch (e: java.io.IOException) {
                        Log.e("StreetCams", e.message ?: e.stackTraceToString())
                        onError()
                        return@downloadImage
                    }

                    // Now that we're finished, release the "pending" status
                    imageDetails.clear()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                    }
                    resolver.update(imgUri, imageDetails, null, null)
                    onComplete()
                    return@downloadImage
                }
                onError()
                return@downloadImage
            },
        )
    }
}