package com.textfield.json.ottawastreetcameras.comparators

import android.location.Location
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlin.math.roundToInt

/**
 * Created by Jason on 24/02/2018.
 */
class SortByDistance(private val userLocation: Location) : Comparator<Camera> {
    override fun compare(cam1: Camera, cam2: Camera): Int {
        val result1 = FloatArray(3)
        val result2 = FloatArray(3)
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, cam1.lat, cam1.lon, result1)
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, cam2.lat, cam2.lon, result2)
        cam1.distance = result1[0].roundToInt()
        cam2.distance = result2[0].roundToInt()
        val result = result1[0].compareTo(result2[0])
        return if (result == 0) SortByName().compare(cam1, cam2) else result
    }
}