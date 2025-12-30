package com.jsontextfield.core.ui

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class CameraRoute(
    val cameras: String = "",
    val isShuffling: Boolean = false,
)
