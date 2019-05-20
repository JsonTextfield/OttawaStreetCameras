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
        return if (Locale.getDefault().displayLanguage.contains("fr")) nameFr else nameEn
    }

    fun getSortableName(): String {
        return getName().replace(Regex("\\W"), "").toUpperCase()
    }
}