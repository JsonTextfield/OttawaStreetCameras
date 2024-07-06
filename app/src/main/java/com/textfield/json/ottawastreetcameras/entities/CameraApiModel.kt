package com.textfield.json.ottawastreetcameras.entities

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
    fun toCamera(): Camera {
        val name = BilingualObject(
            en = nameEn,
            fr = nameFr,
        )
        val neighbourhood = BilingualObject(
            en = neighbourhoodEn,
            fr = neighbourhoodFr,
        )
        return Camera(
            id = id,
            _name = name,
            _neighbourhood = neighbourhood,
            lat = location.lat,
            lon = location.lon,
            _url = url,
        )
    }
}
