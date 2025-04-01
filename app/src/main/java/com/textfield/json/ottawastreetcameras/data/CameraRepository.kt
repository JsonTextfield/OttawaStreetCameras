package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera

class CameraRepository(
    private val dataSource: ICameraDataSource,
    private val prefs: IPreferencesRepository,
) : ICameraRepository {
    private var allCameras = emptyList<Camera>()
    override suspend fun getAllCameras(): List<Camera> {
        val favourites = prefs.getFavourites()
        val hidden = prefs.getHidden()
        if (allCameras.isEmpty()) {
            allCameras = dataSource.getAllCameras()
        }
        allCameras = allCameras.map {
            it.copy(
                isFavourite = it.id in favourites,
                isVisible = it.id !in hidden
            )
        }
        return allCameras
    }
}