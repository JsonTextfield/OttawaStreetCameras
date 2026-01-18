package com.jsontextfield.core.data

import com.jsontextfield.core.entities.Camera
import com.jsontextfield.core.entities.City

interface ICameraRepository {
    suspend fun getAllCameras(city: City): List<Camera>
}