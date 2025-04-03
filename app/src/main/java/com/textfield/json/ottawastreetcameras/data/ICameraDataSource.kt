package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera

interface ICameraDataSource {
    suspend fun getAllCameras(): List<Camera>
}