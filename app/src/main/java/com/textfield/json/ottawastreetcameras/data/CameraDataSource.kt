package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera

interface CameraDataSource {
    suspend fun getAllCameras(): List<Camera>

    suspend fun getCameraById(id: String): Camera
}