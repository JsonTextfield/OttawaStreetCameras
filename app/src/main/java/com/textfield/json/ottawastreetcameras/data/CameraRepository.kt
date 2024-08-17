package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera

interface CameraRepository {
    suspend fun getAllCameras(): List<Camera>

    suspend fun getCamera(id: String): Camera
}