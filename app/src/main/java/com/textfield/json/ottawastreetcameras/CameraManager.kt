package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.textfield.json.ottawastreetcameras.comparators.SortByDistance
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class CameraManager {
    private val tag = "CameraManager"
    var allCameras = ArrayList<Camera>()
        private set
    private val selectedCameras = ArrayList<Camera>()
    var neighbourhoods = ArrayList<Neighbourhood>()
        private set

    var filterMode = FilterMode.VISIBLE
    var searchMode = SearchMode.NONE
    var sortMode = SortMode.NAME
    var viewMode = ViewMode.LIST

    val showSectionIndex: Boolean
        get() {
            return sortMode == SortMode.NAME
                    && searchMode == SearchMode.NONE
                    && filterMode == FilterMode.VISIBLE
        }

    fun isCameraSelected(camera: Camera): Boolean {
        return selectedCameras.contains(camera)
    }

    private fun loadSharedPrefs(context: Context) {
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

    fun favouriteCamera(context: Context, camera: Camera) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("${camera.num}.isFavourite", camera.isFavourite).apply()
        for (cam in allCameras) {
            if (cam == camera) {
                cam.setFavourite(camera.isFavourite)
                break
            }
        }
    }

    fun hideCamera(context: Context, camera: Camera) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("${camera.num}.isVisible", camera.isVisible).apply()
        for (cam in allCameras) {
            if (cam == camera) {
                cam.setVisible(camera.isVisible)
                break
            }
        }
    }

    private fun downloadCameras(context: Context, onComplete: (cameras: ArrayList<Camera>) -> Unit) {
        Log.e(tag, "downloading cameras")
        val url = "https://traffic.ottawa.ca/beta/camera_list"
        val jsonRequest = JsonArrayRequest(url, { response ->
            allCameras = (0 until response.length()).map {
                Camera(response.getJSONObject(it))
            } as ArrayList<Camera>

            onComplete(allCameras)
        }, {
            throw it
        })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(context).addHttp(jsonRequest)
        }
    }

    private fun downloadNeighbourhoods(
        context: Context,
        onComplete: (neighbourhoods: ArrayList<Neighbourhood>) -> Unit
    ) {
        Log.e("STREETCAMS", "downloading neighbourhoods")
        val url =
            "https://services.arcgis.com/G6F8XLCl5KtAlZ2G/arcgis/rest/services/Gen_2_ONS_Boundaries/FeatureServer/0/query?outFields=*&where=1%3D1&f=geojson"
        val jsonObjectRequest = JsonObjectRequest(url,
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

                onComplete(neighbourhoods)
            }, {
                it.printStackTrace()
            })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(context).addHttp(jsonObjectRequest)
        }
    }

    fun downloadAll(
        context: Context,
        onComplete: (cameras: ArrayList<Camera>, neighbourhoods: ArrayList<Neighbourhood>) -> Unit
    ) {
        Log.e("STREETCAMS", "downloading all")
        downloadCameras(context) { cameras ->
            loadSharedPrefs(context)
            downloadNeighbourhoods(context) { onComplete(cameras, it) }
        }
    }

    fun clearSelectedCameras() {
        selectedCameras.clear()
    }

    fun sortDisplayedCameras(sortMode: SortMode, location: Location? = null): ArrayList<Camera> {
        return when (sortMode) {
            SortMode.NAME -> ArrayList(allCameras.sortedWith(SortByName()))
            SortMode.DISTANCE -> {
                if (location != null) {
                    ArrayList(allCameras.sortedWith(SortByDistance(location)))
                }
                ArrayList(allCameras.sortedWith(SortByName()))
            }

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
        fun getInstance(): CameraManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CameraManager().also {
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