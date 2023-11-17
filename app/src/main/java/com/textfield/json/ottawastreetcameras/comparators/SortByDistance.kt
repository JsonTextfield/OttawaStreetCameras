package com.textfield.json.ottawastreetcameras.comparators

import com.textfield.json.ottawastreetcameras.entities.Camera

/**
 * Created by Jason on 24/02/2018.
 */
class SortByDistance : Comparator<Camera> {
    override fun compare(cam1: Camera, cam2: Camera): Int {
        val result = cam1.distance.compareTo(cam2.distance)
        return if (result == 0) SortByName<Camera>().compare(cam1, cam2) else result
    }
}