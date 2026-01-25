package com.jsontextfield.shared.ui.viewmodels

import android.location.Location
import kotlin.math.roundToInt

actual fun getDistanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
    val result = FloatArray(1)
    Location.distanceBetween(
        lat1,
        lon1,
        lat2,
        lon2,
        result
    )
    return result[0].roundToInt()
}