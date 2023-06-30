package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class CameraManager : ViewModel() {

    private var _uiState: MutableLiveData<UIStates> = MutableLiveData<UIStates>(UIStates.LOADING)
    val uiState: LiveData<UIStates>
        get() = _uiState

    fun onUIStateChanged(state: UIStates) {
        _uiState.value = state
    }

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow()

    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }

    private val tag = "CameraManager"

    var allCameras = ArrayList<Camera>()
        private set

    val visibleCameras
        get() = allCameras.filter { it.isVisible }

    val hiddenCameras
        get() = allCameras.filter { !it.isVisible }

    val favouriteCameras
        get() = allCameras.filter { it.isFavourite }

    val displayedCameras
        get() = run {
            val cameras = allCameras.filter {
                when (filterMode.value) {
                    FilterMode.FAVOURITE -> it.isFavourite
                    FilterMode.HIDDEN -> !it.isVisible
                    FilterMode.VISIBLE -> it.isVisible
                    else -> true
                }
            } as ArrayList<Camera>
            when (sortMode.value) {
                SortMode.NAME -> {
                    cameras.sortWith(SortByName())
                }

                SortMode.NEIGHBOURHOOD -> {
                    cameras.sortWith(SortByNeighbourhood())
                }

                SortMode.DISTANCE -> {/*requestLocationPermissions(requestForList) { lastLocation ->
                            cameras.sortWith(SortByDistance(lastLocation))
                        }*/
                }

                else -> {}
            }
            cameras
        }

    private val selectedCameras = ArrayList<Camera>()

    var neighbourhoods = ArrayList<Neighbourhood>()
        private set

    private var _filterMode: MutableLiveData<FilterMode> = MutableLiveData<FilterMode>(FilterMode.VISIBLE)
    val filterMode: LiveData<FilterMode>
        get() = _filterMode

    fun onFilterModeChanged(filterMode: FilterMode) {
        if (filterMode == _filterMode.value) {
            _filterMode.value = FilterMode.VISIBLE
        } else {
            _filterMode.value = filterMode
        }
    }

    private var _searchMode: MutableLiveData<SearchMode> = MutableLiveData<SearchMode>(SearchMode.NONE)
    val searchMode: LiveData<SearchMode>
        get() = _searchMode

    fun onSearchModeChanged(searchMode: SearchMode) {
        if (searchMode == _searchMode.value) {
            _searchMode.value = SearchMode.NONE
        } else {
            _searchMode.value = searchMode
        }
    }

    private var _sortMode: MutableLiveData<SortMode> = MutableLiveData<SortMode>(SortMode.NAME)
    val sortMode: LiveData<SortMode>
        get() = _sortMode

    fun onSortModeChanged(sortMode: SortMode) {
        _sortMode.value = sortMode
    }

    private var _viewMode: MutableLiveData<ViewMode> = MutableLiveData<ViewMode>(ViewMode.LIST)
    val viewMode: LiveData<ViewMode>
        get() = _viewMode

    fun onViewModeChanged(viewMode: ViewMode) {
        _viewMode.value = viewMode
    }

    val showSectionIndex: Boolean
        get() = sortMode.value == SortMode.NAME && searchMode.value == SearchMode.NONE && filterMode.value == FilterMode.VISIBLE

    fun isCameraSelected(camera: Camera): Boolean {
        return selectedCameras.contains(camera)
    }

    private fun loadSharedPrefs(context: Context) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        for (camera in allCameras) {
            camera.isFavourite = sharedPrefs.getBoolean("${camera.num}.isFavourite", false)
            camera.isVisible = sharedPrefs.getBoolean("${camera.num}.isVisible", true)
        }
    }

    fun favouriteCamera(context: Context, camera: Camera) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("${camera.num}.isFavourite", camera.isFavourite).apply()
        for (cam in allCameras) {
            if (cam == camera) {
                cam.isFavourite = camera.isFavourite
                break
            }
        }
    }

    fun hideCamera(context: Context, camera: Camera) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("${camera.num}.isVisible", camera.isVisible).apply()
        for (cam in allCameras) {
            if (cam == camera) {
                cam.isVisible = camera.isVisible
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
            onComplete(allCameras)
        })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(context).addHttp(jsonRequest)
        }
    }

    private fun downloadNeighbourhoods(
        context: Context, onComplete: (neighbourhoods: ArrayList<Neighbourhood>) -> Unit
    ) {
        Log.e("STREETCAMS", "downloading neighbourhoods")
        val url =
            "https://services.arcgis.com/G6F8XLCl5KtAlZ2G/arcgis/rest/services/Gen_2_ONS_Boundaries/FeatureServer/0/query?outFields=*&where=1%3D1&f=geojson"
        val jsonObjectRequest = JsonObjectRequest(url, { response ->
            val jsonArray = response.getJSONArray("features")
            neighbourhoods = (0 until jsonArray.length()).map {
                val neighbourhood = Neighbourhood(jsonArray[it] as JSONObject)
                for (camera in allCameras) {
                    if (neighbourhood.containsCamera(camera)) {
                        camera.neighbourhood = neighbourhood.name
                    }
                }
                neighbourhood
            } as ArrayList<Neighbourhood>

            onComplete(neighbourhoods)
        }, {
            onComplete(neighbourhoods)
        })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(context).addHttp(jsonObjectRequest)
        }
    }

    fun downloadAll(context: Context) {
        if (allCameras.isNotEmpty() && neighbourhoods.isNotEmpty()) {
            return
        }
        Log.e("STREETCAMS", "downloading all")
        downloadCameras(context) { cameras ->
            loadSharedPrefs(context)
            downloadNeighbourhoods(context) {
                if (cameras.isEmpty()) {
                    onUIStateChanged(UIStates.ERROR)
                } else {
                    onUIStateChanged(UIStates.LOADED)
                }
            }
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
                it.isVisible && it.name.contains(query, true)
            })

            SearchMode.NEIGHBOURHOOD -> ArrayList(allCameras.filter {
                it.isVisible && it.neighbourhood.contains(
                    query, true
                )
            })
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
        fun getInstance(): CameraManager = INSTANCE ?: synchronized(this) {
            INSTANCE ?: CameraManager().also {
                INSTANCE = it
            }
        }
    }
}

enum class SortMode { NAME, DISTANCE, NEIGHBOURHOOD, }

enum class FilterMode { VISIBLE, FAVOURITE, HIDDEN, }

enum class SearchMode { NONE, NAME, NEIGHBOURHOOD, }

enum class ViewMode { LIST, MAP, GALLERY }

enum class UIStates { LOADING, LOADED, ERROR }