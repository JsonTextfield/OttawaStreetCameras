package com.textfield.json.ottawastreetcameras.comparators

import com.textfield.json.ottawastreetcameras.entities.Camera

/**
 * Created by Jason on 24/02/2018.
 */
class SortByName : Comparator<Camera> {
    override fun compare(cam1: Camera, cam2: Camera): Int {
        return cam1.sortableName.compareTo(cam2.sortableName)
    }
}