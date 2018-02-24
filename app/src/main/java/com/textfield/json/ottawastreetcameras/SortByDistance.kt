package com.textfield.json.ottawastreetcameras

import android.location.Location

/**
 * Created by Jason on 24/02/2018.
 */
class SortByDistance(location: Location) : Comparator<Camera> {
    private val userLocation = location
    override fun compare(cam1: Camera, cam2: Camera): Int {
        val result1 = FloatArray(3)
        val result2 = FloatArray(3)
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, cam1.lat, cam1.lng, result1)
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, cam2.lat, cam2.lng, result2)
        return result1[0].compareTo(result2[0])
    }
}