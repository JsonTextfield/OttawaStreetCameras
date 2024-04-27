package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.textfield.json.ottawastreetcameras.entities.Camera

interface DownloadService {
    fun download(
        context: Context,
        onComplete: (cameras: List<Camera>) -> Unit = {},
    )
}

object CameraDownloadService : DownloadService {
    private const val TAG = "DownloadService"

    override fun download(context: Context, onComplete: (cameras: List<Camera>) -> Unit) {
        Log.d(TAG, "downloading cameras")
        val url = "https://nacudfxzbqaesoyjfluh.supabase.co/rest/v1/cameras?select=*&city=eq.ottawa"
        val cameraRequest = object : JsonArrayRequest(url, { response ->
            val cameras = (0 until response.length())
                .map { Camera.fromJson(response.getJSONObject(it)) }
                .sortedWith(SortByName)
            onComplete(cameras)
        }, {
            onComplete(ArrayList())
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("apiKey" to context.getString(R.string.supabase_key))
            }
        }

        Volley.newRequestQueue(context).add(cameraRequest)
    }
}