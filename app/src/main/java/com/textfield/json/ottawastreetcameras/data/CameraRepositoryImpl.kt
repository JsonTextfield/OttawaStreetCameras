package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.flow.Flow

class CameraRepositoryImpl(private val dataSource: CameraDataSource = SupabaseCameraDataSource()) : CameraRepository {
    override suspend fun getAllCameras(): Flow<List<Camera>> {
        return dataSource.getAllCameras()
    }
}