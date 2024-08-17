package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera

class CameraRepositoryImpl(private val dataSource: CameraDataSource = SupabaseCameraDataSource()) : CameraRepository {
    override suspend fun getAllCameras(): List<Camera> {
        return dataSource.getAllCameras()
    }

    override suspend fun getCameras(ids: List<String>): List<Camera> {
        return dataSource.getCameras(ids)
    }
}