package com.textfield.json.ottawastreetcameras.entities

import java.util.*

abstract class BilingualObject {
    protected var nameEn = ""
    protected var nameFr = ""
    protected var id = 0

    override fun toString(): String {
        return getName()
    }

    fun getName(): String {
        return if (Locale.getDefault().displayLanguage.contains("fr", true)) nameFr else nameEn
    }

    /**
     * Returns an alphanumeric, uppercase version of the name of the location.
     */
    fun getSortableName(): String {
        return getName().replace(Regex("\\W"), "").uppercase(Locale.ROOT)
    }
}