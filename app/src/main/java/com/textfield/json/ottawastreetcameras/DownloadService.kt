package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood
import org.json.JSONObject

interface DownloadService {
    fun downloadAll(
        context: Context,
        onComplete: (cameras: List<Camera>, neighbourhoods: List<Neighbourhood>) -> Unit,
    )
}

object CameraDownloadService : DownloadService {
    private const val tag = "DownloadService"

    private fun downloadCameras(context: Context, onComplete: (cameras: List<Camera>) -> Unit) {
        Log.d(tag, "downloading cameras")
        val url = "https://traffic.ottawa.ca/beta/camera_list"
        val cameraRequest = JsonArrayRequest(url, { response ->
            val cameras = (0 until response.length())
                .map { Camera(response.getJSONObject(it)) }
                .sortedWith(SortByName())
            onComplete(cameras)
        }, {
            onComplete(ArrayList())
        })
        Volley.newRequestQueue(context).add(cameraRequest)
    }

    private fun downloadNeighbourhoods(context: Context, onComplete: (neighbourhoods: List<Neighbourhood>) -> Unit) {
        Log.d(tag, "downloading neighbourhoods")
        val url =
            "https://services.arcgis.com/G6F8XLCl5KtAlZ2G/arcgis/rest/services/Gen_2_ONS_Boundaries/FeatureServer/0/query?outFields=*&where=1%3D1&f=geojson"
        val neighbourhoodRequest = JsonObjectRequest(url, { response ->
            val jsonArray = response.getJSONArray("features")
            val neighbourhoods = (0 until jsonArray.length())
                .map { Neighbourhood(jsonArray[it] as JSONObject) }
                .sortedWith(SortByName())
            onComplete(neighbourhoods)
        }, {
            onComplete(ArrayList())
        })
        Volley.newRequestQueue(context).add(neighbourhoodRequest)
    }

    override fun downloadAll(
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