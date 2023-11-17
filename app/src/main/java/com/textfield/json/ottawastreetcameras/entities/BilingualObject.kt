package com.textfield.json.ottawastreetcameras.entities

import java.util.Locale

abstract class BilingualObject {
    protected var nameEn = ""
    protected var nameFr = ""
    protected var id = 0

    override fun toString(): String {
        return name
    }

    /**
     * Returns the translated name of the object.
     */
    val name: String
        get() = if (Locale.getDefault().displayLanguage.contains("fr", true) && nameFr.isNotBlank()) nameFr else nameEn


    /**
     * Returns an alphanumeric, uppercase, version of the translated name of the object.
     */
    val sortableName: String
        get() = name.replace(Regex("\\W"), "").uppercase(Locale.ROOT)

}