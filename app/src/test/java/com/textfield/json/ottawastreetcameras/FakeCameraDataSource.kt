package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.data.ICameraDataSource
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera

class FakeCameraDataSource : ICameraDataSource {
    override suspend fun getAllCameras(): List<Camera> {
        return (0 until 100).map {
            Camera(
                id = "$it",
                _name = BilingualObject("Camera $it", "Camera $it"),
            )
        }
    }
}