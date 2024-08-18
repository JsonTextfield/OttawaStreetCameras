package com.textfield.json.ottawastreetcameras.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.textfield.json.ottawastreetcameras.data.CameraRepository
import com.textfield.json.ottawastreetcameras.data.CameraRepositoryImpl
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val cameraRepository: CameraRepository = CameraRepositoryImpl(),
) :
    ViewModel() {

    private var _allCameras = MutableStateFlow<List<Camera>>(emptyList())
    val allCameras: StateFlow<List<Camera>> = _allCameras.asStateFlow()

    private var _cameraList = MutableStateFlow<List<Camera>>(emptyList())
    val cameraList: StateFlow<List<Camera>> = _cameraList.asStateFlow()

    fun getAllCameras() {
        viewModelScope.launch(dispatcher) {
            _allCameras.value = cameraRepository.getAllCameras()
        }
    }

    fun getCameras(ids: String) {
        _cameraList.value = _allCameras.value.filter { camera -> camera.id in ids }
    }

    fun getRandomCamera() {
        _cameraList.value = listOf(_allCameras.value.random())
    }

    companion object {
        val CameraViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CameraViewModel()
            }
        }
    }
}