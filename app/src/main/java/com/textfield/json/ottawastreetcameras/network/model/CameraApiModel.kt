package com.textfield.json.ottawastreetcameras.network.model

import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.City
import kotlinx.serialization.Serializable

@Serializable
data class CameraApiModel(
    private val id: String = "",
    private val nameEn: String = "",
    private val nameFr: String = "",
    private val neighbourhoodEn: String = "",
    private val neighbourhoodFr: String = "",
    private val location: LocationApiModel = LocationApiModel(),
    private val url: String = "",
) {
    fun toCamera(city: City = City.OTTAWA): Camera = Camera(
        id = id,
        _name = BilingualObject(
            en = nameEn,
            fr = nameFr,
        ),
        _neighbourhood = BilingualObject(
            en = neighbourhoodEn,
            fr = neighbourhoodFr,
        ),
        lat = location.lat,
        lon = location.lon,
        _url = url,
    )
}
