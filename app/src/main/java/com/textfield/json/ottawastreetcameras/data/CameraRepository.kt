package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.flow.Flow

interface CameraRepository {
    suspend fun getAllCameras(): Flow<List<Camera>>
}