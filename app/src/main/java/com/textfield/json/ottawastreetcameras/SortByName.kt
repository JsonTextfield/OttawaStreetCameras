package com.textfield.json.ottawastreetcameras

import java.util.*

/**
 * Created by Jason on 24/02/2018.
 */
class SortByName : Comparator<Camera> {
    override fun compare(cam1: Camera, cam2: Camera): Int {
        return cam1.getName().replace("\\W".toRegex(), "").toLowerCase()
                .compareTo(cam2.getName().replace("\\W".toRegex(), "").toLowerCase())
    }
}