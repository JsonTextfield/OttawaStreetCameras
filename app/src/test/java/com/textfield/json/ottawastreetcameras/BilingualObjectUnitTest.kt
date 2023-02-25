package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class BilingualObjectUnitTest {
    private class MyBilingualObject(name: String, nameFr: String) : BilingualObject() {
        init {
            nameEn = name
            this.nameFr = nameFr
        }
    }

    @Test
    fun testSortableName() {
        val name = "name"
        val nameFr = "nameFr"
        val myBilingualObject = MyBilingualObject(name, nameFr)

        Locale.setDefault(Locale("en"))
        assertEquals(myBilingualObject.getSortableName(), name.uppercase(Locale.getDefault()))

        Locale.setDefault(Locale("fr"))
        assertEquals(myBilingualObject.getSortableName(), nameFr.uppercase(Locale.getDefault()))
    }

    @Test
    fun testSortableNameSpecialCharacters() {
        val name = "!@#\$%^&*()n!@#\$%^&*()a!@#\$%^&*()m!@#\$%^&*()e!@#\$%^&*()"
        val nameFr = "n!@#\$%^&*()a!@#\$%^&*()m!@#\$%^&*()e!@#\$%^&*()F!@#\$%^&*()r!@#\$%^&*()"
        val myBilingualObject = MyBilingualObject(name, nameFr)

        Locale.setDefault(Locale("en"))
        assertEquals(myBilingualObject.getSortableName(), "NAME")

        Locale.setDefault(Locale("fr"))
        assertEquals(myBilingualObject.getSortableName(), "NAMEFR")
    }

    @Test
    fun testNameBasedOnLocale() {
        val name = "name"
        val nameFr = "nameFr"
        val myBilingualObject = MyBilingualObject(name, nameFr)

        Locale.setDefault(Locale("en"))
        assertEquals(myBilingualObject.getName(), "name")

        Locale.setDefault(Locale("fr"))
        assertEquals(myBilingualObject.getName(), "nameFr")

        Locale.setDefault(Locale("es"))
        assertEquals(myBilingualObject.getName(), "name")
    }


}