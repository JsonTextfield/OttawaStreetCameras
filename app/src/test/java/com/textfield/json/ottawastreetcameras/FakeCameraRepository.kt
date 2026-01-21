package com.textfield.json.ottawastreetcameras

import com.jsontextfield.core.data.ICameraRepository
import com.jsontextfield.core.entities.BilingualObject
import com.jsontextfield.core.entities.Camera
import com.jsontextfield.core.entities.City

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
                    city = city,
                )
            }
        }
    }
}