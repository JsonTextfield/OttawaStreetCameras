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

    private var _cameraList = MutableStateFlow<List<Camera>>(emptyList())
    val cameraList: StateFlow<List<Camera>> = _cameraList.asStateFlow()

    fun getCameras(id: String) {
        viewModelScope.launch {
            _cameraList.value = cameraRepository.getCameras(id.split(","))
        }
    }
}