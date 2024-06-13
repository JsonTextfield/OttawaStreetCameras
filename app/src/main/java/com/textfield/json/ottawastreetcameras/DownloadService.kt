package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.textfield.json.ottawastreetcameras.entities.Camera
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONArray

interface DownloadService {
    fun download(
        context: Context,
        onComplete: (cameras: List<Camera>) -> Unit = {},
        onError: () -> Unit = {},
    )
}

object CameraDownloadService : DownloadService {
    private const val TAG = "DownloadService"
    private val client = OkHttpClient()

    override fun download(
        context: Context,
        onComplete: (cameras: List<Camera>) -> Unit,
        onError: () -> Unit,
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
        onComplete: (bitmap: Bitmap) -> Unit = {},
        onError: () -> Unit = {},
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
}