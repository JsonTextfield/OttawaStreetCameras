package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.entities.Camera

interface DownloadService {
    fun download(
        context: Context,
        onComplete: (cameras: List<Camera>) -> Unit,
    )
}

object CameraDownloadService : DownloadService {
    private const val tag = "DownloadService"

    override fun download(context: Context, onComplete: (cameras: List<Camera>) -> Unit) {
        Log.d(tag, "downloading cameras")
        val apiKey = context.getString(R.string.supabase_key)
        val url = "https://nacudfxzbqaesoyjfluh.supabase.co/rest/v1/cameras?select=*&city=eq.ottawa&apikey=$apiKey"
        val cameraRequest = JsonArrayRequest(url, { response ->
            val cameras = (0 until response.length())
                .map { Camera.fromJson(response.getJSONObject(it)) }
                .sortedWith(SortByName())
            onComplete(cameras)
        }, {
            onComplete(ArrayList())
        })
        Volley.newRequestQueue(context).add(cameraRequest)
    }
}