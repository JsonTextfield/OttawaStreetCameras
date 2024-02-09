package com.textfield.json.ottawastreetcameras.comparators

import com.textfield.json.ottawastreetcameras.entities.Camera

class SortByNeighbourhood : Comparator<Camera> {
    override fun compare(cam1: Camera, cam2: Camera): Int {
        val result = cam1.neighbourhood.compareTo(cam2.neighbourhood)
        return if (result == 0) SortByName().compare(cam1, cam2) else result
    }
}