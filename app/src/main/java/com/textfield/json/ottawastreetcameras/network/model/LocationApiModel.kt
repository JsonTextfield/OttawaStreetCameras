package com.textfield.json.ottawastreetcameras.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationApiModel(
    val lat: Double = 0.0,
    val lon: Double = 0.0,
)
