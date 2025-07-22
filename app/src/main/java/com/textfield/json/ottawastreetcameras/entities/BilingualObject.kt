package com.textfield.json.ottawastreetcameras.entities

import java.util.Locale

data class BilingualObject(
    private val en: String = "",
    private val fr: String = "",
) {
    /**
     * Returns the translated name of the object.
     */
    val name: String
        get() {
            return if ("fr" in Locale.getDefault().displayLanguage.lowercase() && fr.isNotBlank() || en.isBlank()) fr
            else en
        }

    /**
     * Returns an alphanumeric, uppercase, version of the translated name of the object.
     */
    val sortableName: String
        get() = name.uppercase(Locale.ROOT).replace(Regex("[^0-9A-ZÀ-Ö]"), "")

}