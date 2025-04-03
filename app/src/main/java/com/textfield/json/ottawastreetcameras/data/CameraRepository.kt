package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera

class CameraRepository(
    private val dataSource: ICameraDataSource,
) : ICameraRepository {
    private var allCameras = emptyList<Camera>()
    override suspend fun getAllCameras(): List<Camera> {
        if (allCameras.isEmpty()) {
            allCameras = dataSource.getAllCameras()
        }
        return allCameras
    }
}