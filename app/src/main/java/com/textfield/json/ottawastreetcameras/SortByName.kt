package com.textfield.json.ottawastreetcameras

import java.util.*

/**
 * Created by Jason on 24/02/2018.
 */
class SortByName(lang: Boolean) : Comparator<Camera> {
    private val french = lang
    override fun compare(cam1: Camera, cam2: Camera): Int {
        return if (french) cam1.nameFr.replace("\\W".toRegex(), "").toLowerCase()
                .compareTo(cam2.nameFr.replace("\\W".toRegex(), "").toLowerCase())
        else cam1.name.replace("\\W".toRegex(), "").toLowerCase()
                .compareTo(cam2.name.replace("\\W".toRegex(), "").toLowerCase())
    }
}