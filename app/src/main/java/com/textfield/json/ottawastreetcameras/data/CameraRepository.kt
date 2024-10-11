package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera

class CameraRepository(private val dataSource: ICameraDataSource) : ICameraRepository {
    override suspend fun getAllCameras(): List<Camera> {
        return dataSource.getAllCameras()
    }
}