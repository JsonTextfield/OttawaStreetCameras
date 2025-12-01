package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.City

interface ICameraRepository {
    suspend fun getAllCameras(city: City = City.OTTAWA): List<Camera>
}