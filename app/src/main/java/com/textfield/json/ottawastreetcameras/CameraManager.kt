package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood
import kotlinx.coroutines.*
import org.json.JSONObject

class CameraManager private constructor(var context: Context) {
    val tag = "CameraManager"
    var allCameras = ArrayList<Camera>()
        private set
    private val selectedCameras = ArrayList<Camera>()
    var neighbourhoods = ArrayList<Neighbourhood>()
    var filterMode = FilterMode.VISIBLE
    var searchMode = SearchMode.NONE
        private set
    var sortMode = SortMode.NAME
    var viewMode = ViewMode.LIST

    private fun loadSharedPrefs() {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        for (camera in allCameras) {
            camera.setFavourite(
                sharedPrefs.getBoolean("${camera.num}.isFavourite", false)
            )
            camera.setVisible(
                sharedPrefs.getBoolean("${camera.num}.isVisible", true)
            )
        }
    }

    private fun downloadCameras(onComplete: () -> Unit) {
        Log.e(tag, "downloading cameras")
        val jsonRequest = JsonArrayRequest("https://traffic.ottawa.ca/beta/camera_list", { response ->
            allCameras = (0 until response.length()).map {
                Camera(response.getJSONObject(it))
            } as ArrayList<Camera>

            onComplete()
        }, {
            throw it
        })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(context).addHttp(jsonRequest)
        }
    }

    private fun downloadNeighbourhoods(onComplete: () -> Unit) {
        Log.e("STREETCAMS", "downloading neighbourhoods")
        val jsonObjectRequest = JsonObjectRequest(
            "https://services.arcgis.com/G6F8XLCl5KtAlZ2G/arcgis/rest/services/Gen_2_ONS_Boundaries/FeatureServer/0/query?outFields=*&where=1%3D1&f=geojson",
            { response ->
                val jsonArray = response.getJSONArray("features")
                neighbourhoods = (0 until jsonArray.length()).map {
                    val neighbourhood = Neighbourhood(jsonArray[it] as JSONObject)
                    for (camera in allCameras) {
                        if (neighbourhood.containsCamera(camera)) {
                            camera.neighbourhood = neighbourhood.getName()
                        }
                    }
                    neighbourhood
                } as ArrayList<Neighbourhood>

                onComplete()
            }, {
                it.printStackTrace()
            })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(context).addHttp(jsonObjectRequest)
        }
    }

    fun downloadAll(onComplete: () -> Unit) {
        Log.e("STREETCAMS", "downloading all")
        downloadCameras { downloadNeighbourhoods(onComplete) }
    }

    fun sortDisplayedCameras(sortMode: SortMode): ArrayList<Camera> {
        return when (sortMode) {
            SortMode.NAME -> ArrayList(allCameras.sortedWith(SortByName()))
            SortMode.DISTANCE -> ArrayList(/*allCameras.sortedWith(SortByDistance(location))*/)
            SortMode.NEIGHBOURHOOD -> ArrayList(allCameras.sortedWith(SortByNeighbourhood()))
        }
    }

    fun searchDisplayedCameras(searchMode: SearchMode, query: String = ""): ArrayList<Camera> {
        return when (searchMode) {
            SearchMode.NONE -> ArrayList(allCameras.filter { it.isVisible })
            SearchMode.NAME -> ArrayList(allCameras.filter {
                it.isVisible && it.getName().contains(query, true)
            })

            SearchMode.NEIGHBOURHOOD -> ArrayList(allCameras.filter {
                it.isVisible && it.neighbourhood.contains(
                    query,
                    true
                )
            })
        }
    }

    fun filterDisplayedCameras(filterMode: FilterMode): ArrayList<Camera> {
        if (this.filterMode == filterMode) {
            this.filterMode = FilterMode.VISIBLE
        }
        this.filterMode = filterMode
        return when (this.filterMode) {
            FilterMode.VISIBLE -> ArrayList(allCameras.filter { it.isVisible })
            FilterMode.FAVOURITE -> ArrayList(allCameras.filter { it.isFavourite })
            FilterMode.HIDDEN -> ArrayList(allCameras.filter { !it.isVisible })
        }
    }

    fun selectCamera(camera: Camera) {
        if (selectedCameras.contains(camera)) {
            selectedCameras.remove(camera)
        } else {
            selectedCameras.add(camera)
        }
    }

    fun getSelectedCameras(): ArrayList<Camera> {
        return ArrayList<Camera>(selectedCameras)
    }


    companion object {
        private var INSTANCE: CameraManager? = null
        fun getInstance(context: Context): CameraManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CameraManager(context).also {
                    INSTANCE = it
                }
            }
    }
}

enum class SortMode {
    NAME, DISTANCE, NEIGHBOURHOOD,
}

enum class FilterMode {
    VISIBLE, FAVOURITE, HIDDEN,
}

enum class SearchMode {
    NONE, NAME, NEIGHBOURHOOD,
}

enum class ViewMode {
    LIST, MAP, GALLERY
}