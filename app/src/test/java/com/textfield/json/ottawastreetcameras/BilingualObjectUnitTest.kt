package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import org.junit.Before
import org.junit.Test
import java.util.*

class BilingualObjectUnitTest {
    private class MyBilingualObject(name: String, nameFr: String) : BilingualObject() {
        init {
            nameEn = name
            this.nameFr = nameFr
        }
    }
    private lateinit var bilingualObject: MyBilingualObject

    @Before
    fun setup() {
        bilingualObject = MyBilingualObject("*English", "%French")
    }

    @Test
    fun testEnglish() {
        Locale.setDefault(Locale("en"))
        assert(bilingualObject.getName() == "*English")
        assert(bilingualObject.getSortableName() == "ENGLISH") {
            print("Actual was ${bilingualObject.getSortableName()}")
        }
    }

    @Test
    fun testFrench() {
        Locale.setDefault(Locale("fr"))
        assert(bilingualObject.getName() == "%French")
        assert(bilingualObject.getSortableName() == "FRENCH") {
            print("Actual was ${bilingualObject.getSortableName()}")
        }
    }

    @Test
    fun testOther() {
        Locale.setDefault(Locale("es"))
        assert(bilingualObject.getName() == "*English")
        assert(bilingualObject.getSortableName() == "ENGLISH") {
            print("Actual was ${bilingualObject.getSortableName()}")
        }
    }
}