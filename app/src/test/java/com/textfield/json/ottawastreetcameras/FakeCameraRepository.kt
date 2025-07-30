package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.data.ICameraRepository
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera

class FakeCameraRepository : ICameraRepository {
    var returnEmptyList = false
    override suspend fun getAllCameras(): List<Camera> {
        return if (returnEmptyList) {
            emptyList()
        } else {
            List(100) {
                Camera(
                    id = "$it",
                    _name = BilingualObject("Camera $it", "Camera $it"),
                )
            }
        }
    }
}