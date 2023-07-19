package com.textfield.json.ottawastreetcameras.comparators

import com.textfield.json.ottawastreetcameras.entities.BilingualObject

/**
 * Created by Jason on 24/02/2018.
 */
class SortByName : Comparator<BilingualObject> {
    override fun compare(o1: BilingualObject, o2: BilingualObject): Int {
        return o1.sortableName.compareTo(o2.sortableName)
    }
}