package com.jsontextfield.shared.network.model

import com.jsontextfield.shared.entities.BilingualObject
import com.jsontextfield.shared.entities.Camera
import com.jsontextfield.shared.entities.City
import kotlinx.serialization.Serializable

@Serializable
data class CameraApiModel(
    private val id: String = "",
    private val city: String = "",
    private val nameEn: String = "",
    private val nameFr: String = "",
    private val neighbourhoodEn: String = "",
    private val neighbourhoodFr: String = "",
    private val location: LocationApiModel = LocationApiModel(),
    private val url: String = "",
) {
    fun toCamera(): Camera = Camera(
        id = id,
        city = City.entries.firstOrNull { it.cityName == city } ?: City.OTTAWA,
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
