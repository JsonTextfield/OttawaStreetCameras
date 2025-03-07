package com.textfield.json.ottawastreetcameras.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.textfield.json.ottawastreetcameras.data.ICameraRepository
import com.textfield.json.ottawastreetcameras.entities.Camera
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cameraRepository: ICameraRepository,
) :
    ViewModel() {

    private var _allCameras = MutableStateFlow<List<Camera>>(emptyList())
    val allCameras: StateFlow<List<Camera>> = _allCameras.asStateFlow()

    private var _cameraList = MutableStateFlow<List<Camera>>(emptyList())
    val cameraList: StateFlow<List<Camera>> = _cameraList.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            _allCameras.value = cameraRepository.getAllCameras()
        }
    }

    fun getCameras(ids: String) {
        viewModelScope.launch(dispatcher) {
            _cameraList.value = _allCameras.value.filter { camera -> camera.id in ids }
            _cameraList.value = _allCameras.value.filter { camera -> camera.id in ids }
        }
    }

    fun getRandomCamera() {
        viewModelScope.launch(dispatcher) {
            _cameraList.value = listOf(_allCameras.value.random())
        }
    }
}