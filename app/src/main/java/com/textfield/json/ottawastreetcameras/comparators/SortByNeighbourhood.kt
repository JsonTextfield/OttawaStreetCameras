package com.textfield.json.ottawastreetcameras.comparators

import com.textfield.json.ottawastreetcameras.entities.Camera

class SortByNeighbourhood : Comparator<Camera> {
    override fun compare(camera1: Camera, camera2: Camera): Int {
        val result = camera1.neighbourhood.compareTo(camera2.neighbourhood)
        return if (result == 0) SortByName().compare(camera1, camera2) else result
    }
}