package com.textfield.json.ottawastreetcameras

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.comparators.SortByNeighbourhood
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class CameraManager : ViewModel() {
    private val tag = "CameraManager"
    private var _cameraState = MutableLiveData(
        CameraState(
            allCameras = ArrayList(),
            displayedCameras = ArrayList(),
            selectedCameras = ArrayList(),
            neighbourhoods = ArrayList(),
            uiState = UIState.INITIAL,
            sortMode = SortMode.NAME,
            searchMode = SearchMode.NONE,
            filterMode = FilterMode.VISIBLE,
            viewMode = ViewMode.LIST,
            lastUpdated = 0
        )
    )

    val cameraState: LiveData<CameraState>
        get() = _cameraState

    fun changeViewMode(viewMode: ViewMode) {
        _cameraState.value = _cameraState.value?.copy(viewMode = viewMode)
    }

    fun changeSortMode(sortMode: SortMode) {
        val displayedCameras = ArrayList<Camera>(_cameraState.value?.displayedCameras ?: ArrayList())
        when (sortMode) {
            SortMode.NAME -> displayedCameras.sortWith(SortByName())
            SortMode.DISTANCE -> displayedCameras.sortWith(SortByName())
            SortMode.NEIGHBOURHOOD -> displayedCameras.sortWith(SortByNeighbourhood())
        }
        _cameraState.value = _cameraState.value?.copy(
            sortMode = sortMode,
            displayedCameras = displayedCameras,
        )
    }

    fun changeSearchMode(searchMode: SearchMode) {
        _cameraState.value =
            _cameraState.value?.copy(searchMode = if (searchMode == _cameraState.value?.searchMode) SearchMode.NONE else searchMode)
    }

    fun changeFilterMode(filterMode: FilterMode) {
        val fm = if (filterMode == _cameraState.value?.filterMode) FilterMode.VISIBLE else filterMode
        val displayedCameras =
            when (fm) {
                FilterMode.VISIBLE -> _cameraState.value?.visibleCameras
                FilterMode.HIDDEN -> _cameraState.value?.hiddenCameras
                FilterMode.FAVOURITE -> _cameraState.value?.favouriteCameras
            } ?: ArrayList()
        _cameraState.value = _cameraState.value?.copy(
            displayedCameras = displayedCameras,
            filterMode = fm,
        )
    }

    private fun loadSharedPrefs(context: Context) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val allCameras = ArrayList<Camera>(_cameraState.value?.allCameras ?: ArrayList())
        for (camera in allCameras) {
            camera.isFavourite = sharedPrefs.getBoolean("${camera.num}.isFavourite", false)
            camera.isVisible = sharedPrefs.getBoolean("${camera.num}.isVisible", true)
        }
        _cameraState.value = _cameraState.value?.copy(allCameras = allCameras)
    }

    fun favouriteCamera(context: Context, camera: Camera) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("${camera.num}.isFavourite", camera.isFavourite).apply()
        val updatedCameras = ArrayList<Camera>(_cameraState.value?.allCameras ?: ArrayList())
        val updatedDisplayedCameras = ArrayList<Camera>(_cameraState.value?.displayedCameras ?: ArrayList())
        for (cam in updatedCameras) {
            if (cam == camera) {
                cam.isFavourite = !cam.isFavourite
                break
            }
        }
        for (cam in updatedDisplayedCameras) {
            if (cam == camera) {
                cam.isFavourite = !cam.isFavourite
                break
            }
        }
        _cameraState.value =
            _cameraState.value?.copy(allCameras = updatedCameras, displayedCameras = updatedDisplayedCameras)
    }

    fun hideCamera(context: Context, camera: Camera) {
        val sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("${camera.num}.isVisible", camera.isVisible).apply()
        val updatedCameras = ArrayList<Camera>(_cameraState.value?.allCameras ?: ArrayList())
        val updatedDisplayedCameras = ArrayList<Camera>(_cameraState.value?.displayedCameras ?: ArrayList())
        for (cam in updatedCameras) {
            if (cam == camera) {
                cam.isVisible = !cam.isVisible
                break
            }
        }
        for (cam in updatedDisplayedCameras) {
            if (cam == camera) {
                cam.isVisible = !cam.isVisible
                break
            }
        }
        _cameraState.value =
            _cameraState.value?.copy(allCameras = updatedCameras, displayedCameras = updatedDisplayedCameras)
    }

    fun favouriteSelectedCameras(context: Context) {
        _cameraState.value?.selectedCameras?.let {
            for (camera in it) {
                favouriteCamera(context, camera)
            }
        }
    }

    fun hideSelectedCameras(context: Context) {
        _cameraState.value?.selectedCameras?.let {
            for (camera in it) {
                hideCamera(context, camera)
            }
        }
    }

    fun selectCamera(camera: Camera) {
        val selectedCameras = ArrayList<Camera>(_cameraState.value?.selectedCameras ?: ArrayList())
        if (selectedCameras.contains(camera)) {
            selectedCameras.remove(camera)
        }
        else {
            selectedCameras.add(camera)
        }
        _cameraState.value = _cameraState.value?.copy(selectedCameras = selectedCameras)
    }

    fun selectAllCameras() {
        _cameraState.value =
            _cameraState.value?.copy(selectedCameras = _cameraState.value?.displayedCameras ?: ArrayList())
    }

    fun clearSelectedCameras() {
        _cameraState.value = _cameraState.value?.copy(selectedCameras = ArrayList())
    }

    fun searchCameras(query: String = "") {
        _cameraState.value = when (_cameraState.value?.searchMode) {
            SearchMode.NONE -> {
                _cameraState.value?.allCameras?.filter { it.isVisible }
            }

            SearchMode.NAME -> {
                _cameraState.value?.allCameras?.filter {
                    it.isVisible && it.name.contains(query, true)
                }
            }

            SearchMode.NEIGHBOURHOOD -> {
                _cameraState.value?.allCameras?.filter {
                    it.isVisible && it.neighbourhood.contains(query, true)
                }
            }

            else -> {
                _cameraState.value?.displayedCameras ?: ArrayList<Camera>()
            }
        }?.let {
            _cameraState.value?.copy(
                displayedCameras = it
            )
        }
    }

    private fun downloadCameras(context: Context, onComplete: (cameras: ArrayList<Camera>) -> Unit) {
        Log.e(tag, "downloading cameras")
        val url = "https://traffic.ottawa.ca/beta/camera_list"
        val jsonRequest = JsonArrayRequest(url, { response ->
            val cameras = (0 until response.length())
                .map {
                    Camera(response.getJSONObject(it))
                } as ArrayList<Camera>
            _cameraState.value =
                _cameraState.value?.copy(
                    allCameras = cameras.sortedWith(SortByName()),
                    displayedCameras = cameras.sortedWith(SortByName()),
                )

            onComplete(cameras)
        }, {
            onComplete(ArrayList())
        })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(context).addHttp(jsonRequest)
        }
    }

    private fun downloadNeighbourhoods(
        context: Context, onComplete: (neighbourhoods: ArrayList<Neighbourhood>) -> Unit,
    ) {
        Log.e("STREETCAMS", "downloading neighbourhoods")
        val url =
            "https://services.arcgis.com/G6F8XLCl5KtAlZ2G/arcgis/rest/services/Gen_2_ONS_Boundaries/FeatureServer/0/query?outFields=*&where=1%3D1&f=geojson"
        val jsonObjectRequest = JsonObjectRequest(url, { response ->
            val jsonArray = response.getJSONArray("features")
            val neighbourhoods = (0 until jsonArray.length()).map {
                val neighbourhood = Neighbourhood(jsonArray[it] as JSONObject)
                for (camera in _cameraState.value?.allCameras!!) {
                    if (neighbourhood.containsCamera(camera)) {
                        camera.neighbourhood = neighbourhood.name
                    }
                }
                neighbourhood
            } as ArrayList<Neighbourhood>
            _cameraState.value = _cameraState.value?.copy(neighbourhoods = neighbourhoods)
            onComplete(neighbourhoods)
        }, {
            onComplete(ArrayList())
        })
        CoroutineScope(Dispatchers.IO).launch {
            StreetCamsRequestQueue.getInstance(context).addHttp(jsonObjectRequest)
        }
    }

    fun downloadAll(context: Context) {
        _cameraState.value = _cameraState.value?.copy(uiState = UIState.LOADING)
        if (_cameraState.value?.allCameras?.isEmpty() == true
            || _cameraState.value?.neighbourhoods?.isEmpty() == true
        ) {
            Log.e("STREETCAMS", "downloading all")
            downloadCameras(context) { cameras ->
                loadSharedPrefs(context)
                downloadNeighbourhoods(context) {
                    if (cameras.isEmpty()) {
                        _cameraState.value = _cameraState.value?.copy(uiState = UIState.ERROR)
                    }
                    else {
                        _cameraState.value = _cameraState.value?.copy(uiState = UIState.LOADED)
                    }
                }
            }
        }
        else {
            _cameraState.value = _cameraState.value?.copy(uiState = UIState.LOADED)
        }
    }

    companion object {
        @Volatile private var INSTANCE: CameraManager? = null
        fun getInstance(): CameraManager = INSTANCE ?: synchronized(this) {
            INSTANCE ?: CameraManager().also {
                INSTANCE = it
            }
        }
    }
}