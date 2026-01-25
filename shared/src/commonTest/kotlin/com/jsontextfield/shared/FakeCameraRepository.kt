package com.jsontextfield.shared

import com.jsontextfield.shared.data.ICameraRepository
import com.jsontextfield.shared.entities.BilingualObject
import com.jsontextfield.shared.entities.Camera
import com.jsontextfield.shared.entities.City

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