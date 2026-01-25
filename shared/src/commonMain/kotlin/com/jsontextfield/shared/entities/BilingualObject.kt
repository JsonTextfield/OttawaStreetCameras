package com.jsontextfield.shared.entities

import androidx.compose.ui.text.intl.Locale

data class BilingualObject(
    private val en: String = "",
    private val fr: String = "",
) {
    /**
     * Returns the translated name of the object.
     */
    val name: String
        get() {
            return if ("fr" in Locale.current.language.lowercase() && fr.isNotBlank() || en.isBlank()) fr
            else en
        }

    /**
     * Returns an alphanumeric, uppercase, version of the translated name of the object.
     */
    val sortableName: String
        get() = name.uppercase().replace(Regex("[^0-9A-ZÀ-Ö]"), "")

}