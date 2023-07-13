package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

object DownloadService {
    private const val tag = "DownloadService"

    private fun downloadCameras(context: Context, onComplete: (cameras: List<Camera>) -> Unit) {
        Log.d(tag, "downloading cameras")
        val url = "https://traffic.ottawa.ca/beta/camera_list"
        val jsonRequest = JsonArrayRequest(url, { response ->
            val cameras = (0 until response.length())
                .map { Camera(response.getJSONObject(it)) }
                .sortedWith(SortByName())
            onComplete(cameras)
        }, {
            onComplete(ArrayList())
        })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(context).addHttp(jsonRequest)
        }
    }

    private fun downloadNeighbourhoods(
        context: Context, onComplete: (neighbourhoods: List<Neighbourhood>) -> Unit,
    ) {
        Log.d(tag, "downloading neighbourhoods")
        val url =
            "https://services.arcgis.com/G6F8XLCl5KtAlZ2G/arcgis/rest/services/Gen_2_ONS_Boundaries/FeatureServer/0/query?outFields=*&where=1%3D1&f=geojson"
        val jsonObjectRequest = JsonObjectRequest(url, { response ->
            val jsonArray = response.getJSONArray("features")
            val neighbourhoods = (0 until jsonArray.length())
                .map {
                    Neighbourhood(jsonArray[it] as JSONObject)
                }
            onComplete(neighbourhoods)
        }, {
            onComplete(ArrayList())
        })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(context).addHttp(jsonObjectRequest)
        }
    }

    fun downloadAll(
        context: Context,
        onComplete: (cameras: List<Camera>, neighbourhoods: List<Neighbourhood>) -> Unit,
    ) {
        Log.d(tag, "downloading all")
        downloadCameras(context) { cameras ->
            downloadNeighbourhoods(context) { neighbourhoods ->
                onComplete(cameras, neighbourhoods)
            }
        }
    }
}