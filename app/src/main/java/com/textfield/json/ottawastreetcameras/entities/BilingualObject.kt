package com.textfield.json.ottawastreetcameras.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class BilingualObject(val en: String = "", val fr: String = "") : Parcelable {
    /**
     * Returns the translated name of the object.
     */
    val name: String
        get() {
            return if (Locale.getDefault().displayLanguage.contains(
                    "fr",
                    true
                ) && fr.isNotBlank() || en.isBlank()
            ) fr
            else en
        }

    /**
     * Returns an alphanumeric, uppercase, version of the translated name of the object.
     */
    val sortableName: String
        get() = name.uppercase(Locale.ROOT).replace(Regex("[^0-9A-ZÀ-Ö]"), "")

}