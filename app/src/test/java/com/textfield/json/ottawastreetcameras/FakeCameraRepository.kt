package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.data.ICameraRepository
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.City

class FakeCameraRepository : ICameraRepository {
    var returnEmptyList = false
    override suspend fun getAllCameras(city: City): List<Camera> {
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