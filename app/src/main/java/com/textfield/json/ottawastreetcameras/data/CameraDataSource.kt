package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera

interface CameraDataSource {
    suspend fun getAllCameras(): List<Camera>

    suspend fun getCameras(ids: List<String>): List<Camera>
}