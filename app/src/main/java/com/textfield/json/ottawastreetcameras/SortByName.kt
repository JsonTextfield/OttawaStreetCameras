package com.textfield.json.ottawastreetcameras

/**
 * Created by Jason on 24/02/2018.
 */
class SortByName : Comparator<Camera> {
    override fun compare(cam1: Camera, cam2: Camera): Int {
        return cam1.getName().replace(Regex("\\W"), "").toLowerCase()
                .compareTo(cam2.getName().replace(Regex("\\W"), "").toLowerCase())
    }
}