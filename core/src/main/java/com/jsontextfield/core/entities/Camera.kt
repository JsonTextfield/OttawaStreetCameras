package com.jsontextfield.core.entities

import java.util.UUID
import kotlin.math.roundToInt

/**
 * Created by Jason on 25/04/2016.
 */
data class Camera(
    val id: String = UUID.randomUUID().toString(),
    private val _name: BilingualObject = BilingualObject(),
    private val _neighbourhood: BilingualObject = BilingualObject(),
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    private val _url: String = "",
    val isFavourite: Boolean = false,
    val isVisible: Boolean = true,
    val isSelected: Boolean = false,
    val distance: Int = -1
) {

    val name: String get() = _name.name
    val sortableName: String get() = _name.sortableName
    val neighbourhood: String get() = _neighbourhood.name
    val url: String get() = "$_url&timems=${System.currentTimeMillis()}"
    val preview: String get() = _url

    val distanceString: String
        get() {
            val distance = this.distance.toDouble()
            return when {
                distance < 0 -> ""
                distance > 9_000_000 -> ">9000\nkm"
                distance >= 100_000 -> "${(distance / 1000).roundToInt()}\nkm"
                distance >= 500 -> "${(distance / 100).roundToInt() / 10.0}\nkm"
                else -> "${distance.roundToInt()}\nm"
            }
        }

    override fun toString(): String = name
}
