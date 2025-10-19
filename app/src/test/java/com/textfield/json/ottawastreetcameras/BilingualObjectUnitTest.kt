package com.textfield.json.ottawastreetcameras

import com.jsontextfield.core.entities.BilingualObject
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class BilingualObjectUnitTest {

    @Test
    fun testsortableName() {
        val nameEn = "name"
        val nameFr = "nameFr"
        val bilingualObject = BilingualObject(nameEn, nameFr)

        Locale.setDefault(Locale("en"))
        assertEquals(bilingualObject.sortableName, nameEn.uppercase(Locale.getDefault()))

        Locale.setDefault(Locale("fr"))
        assertEquals(bilingualObject.sortableName, nameFr.uppercase(Locale.getDefault()))
    }

    @Test
    fun testSortableNameSpecialCharacters() {
        val nameEn = "!@#$%^&*()n!@#$%^&*()a!@#$%^&*()m!@#$%^&*()e!@#$%^&*()"
        val nameFr = "n!@#$%^&*()a!@#$%^&*()m!@#$%^&*()e!@#$%^&*()F!@#$%^&*()r!@#$%^&*()"
        val myBilingualObject = BilingualObject(nameEn, nameFr)

        Locale.setDefault(Locale("en"))
        assertEquals(myBilingualObject.sortableName, "NAME")

        Locale.setDefault(Locale("fr"))
        assertEquals(myBilingualObject.sortableName, "NAMEFR")
    }

    @Test
    fun testNameBasedOnLocale() {
        val nameEn = "name"
        val nameFr = "nameFr"
        val myBilingualObject = BilingualObject(nameEn, nameFr)

        Locale.setDefault(Locale("en"))
        assertEquals(myBilingualObject.name, "name")

        Locale.setDefault(Locale("fr"))
        assertEquals(myBilingualObject.name, "nameFr")

        Locale.setDefault(Locale("es"))
        assertEquals(myBilingualObject.name, "name")
    }


}