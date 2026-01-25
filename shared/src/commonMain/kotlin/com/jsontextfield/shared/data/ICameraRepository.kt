package com.jsontextfield.shared.data

import com.jsontextfield.shared.entities.Camera
import com.jsontextfield.shared.entities.City

interface ICameraRepository {
    suspend fun getAllCameras(city: City): List<Camera>
}