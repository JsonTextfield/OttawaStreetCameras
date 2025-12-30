package com.jsontextfield.core.ui

import com.jsontextfield.core.entities.City
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class CameraRoute(
    val cameras: String = "",
    val isShuffling: Boolean = false,
)

@Serializable
data class CitySelectionRoute(
    val selectedCity: City = City.TORONTO,
)
