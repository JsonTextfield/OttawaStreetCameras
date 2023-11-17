package com.textfield.json.ottawastreetcameras.comparators

import com.textfield.json.ottawastreetcameras.entities.BilingualObject

/**
 * Created by Jason on 24/02/2018.
 */
class SortByName<T: BilingualObject> : Comparator<T> {
    override fun compare(o1: T, o2: T): Int {
        return o1.sortableName.compareTo(o2.sortableName)
    }
}