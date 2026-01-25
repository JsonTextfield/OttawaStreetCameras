package com.jsontextfield.shared.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.shared.data.ICameraRepository
import com.jsontextfield.shared.data.IPreferencesRepository
import com.jsontextfield.shared.entities.Camera
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CameraViewModel(
    private val cameraRepository: ICameraRepository,
    private val prefRepository: IPreferencesRepository,
    private val cameraIds: String = "",
    private val isShuffling: Boolean = false,
) : ViewModel() {

    private var _allCameras = MutableStateFlow<List<Camera>>(emptyList())
    val allCameras: StateFlow<List<Camera>> = _allCameras.asStateFlow()

    private var _cameraList = MutableStateFlow<List<Camera>>(emptyList())
    val cameraList: StateFlow<List<Camera>> = _cameraList.asStateFlow()

    var update by mutableStateOf(false)

    private var job: Job? = null

    init {
        prefRepository.getCity().map { city ->
            _allCameras.value = cameraRepository.getAllCameras(city)
            getCameras()
            job = job ?: viewModelScope.launch {
                while (true) {
                    if (isShuffling) {
                        getRandomCamera()
                    }
                    update = !update
                    delay(6000)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getCameras() {
        _cameraList.value = _allCameras.value.filter { camera -> camera.id in cameraIds }
    }

    private fun getRandomCamera() {
        _cameraList.value = listOf(_allCameras.value.random())
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        job = null
    }
}