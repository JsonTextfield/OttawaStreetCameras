package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera

class CameraRepositoryImpl(private val dataSource: CameraDataSource) : CameraRepository {
    override suspend fun getAllCameras(): List<Camera> {
        return dataSource.getAllCameras()
    }
}