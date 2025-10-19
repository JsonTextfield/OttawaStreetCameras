package com.jsontextfield.core.network.model

import com.jsontextfield.core.entities.BilingualObject
import com.jsontextfield.core.entities.Camera
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
    fun toCamera(): Camera = Camera(
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
