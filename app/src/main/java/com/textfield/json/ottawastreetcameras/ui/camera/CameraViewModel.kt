package com.textfield.json.ottawastreetcameras.ui.camera

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.textfield.json.ottawastreetcameras.data.ICameraRepository
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CameraViewModel(
    private val cameraRepository: ICameraRepository,
    private val cameraIds: String = "",
    private val isShuffling: Boolean = false,
) : ViewModel() {
    private var _allCameras = MutableStateFlow<List<Camera>>(emptyList())
    val allCameras: StateFlow<List<Camera>> = _allCameras
    private var _cameraList = MutableStateFlow<List<Camera>>(emptyList())
    val cameraList: StateFlow<List<Camera>> = _cameraList.asStateFlow()

    var update by mutableStateOf(false)

    init {
        viewModelScope.launch {
            val allCameras = cameraRepository.getAllCameras()
            _allCameras.value = allCameras
            _cameraList.value = allCameras.filter { camera ->
                camera.id in cameraIds
            }
            while (isActive) {
                if (isShuffling) {
                    getRandomCamera()
                }
                update = !update
                delay(6000)
            }
        }
    }

    private fun getRandomCamera() {
        _cameraList.value = listOf(allCameras.value.random())
    }
}