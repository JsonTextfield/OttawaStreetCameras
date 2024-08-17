package com.textfield.json.ottawastreetcameras.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.textfield.json.ottawastreetcameras.data.CameraRepository
import com.textfield.json.ottawastreetcameras.data.CameraRepositoryImpl
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel(private val cameraRepository: CameraRepository = CameraRepositoryImpl()) :
    ViewModel() {

    private var _allCameras = MutableStateFlow<List<Camera>>(emptyList())
    val allCameras: StateFlow<List<Camera>> = _allCameras.asStateFlow()

    private var _cameraList = MutableStateFlow<List<Camera>>(emptyList())
    val cameraList: StateFlow<List<Camera>> = _cameraList.asStateFlow()

    fun getCameras(ids: String) {
        viewModelScope.launch {
            _allCameras.value = cameraRepository.getAllCameras()
            _cameraList.value = _allCameras.value.filter { camera -> camera.id in ids }
        }
    }

    fun getRandomCamera() {
        viewModelScope.launch {
            _cameraList.value = listOf(cameraRepository.getAllCameras().random())
        }
    }
}