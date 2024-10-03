package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera

interface ICameraRepository {
    suspend fun getAllCameras(): List<Camera>
}