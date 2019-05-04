package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.BilingualObject
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
    fun test1() {
        val b = MyBilingualObject("*English", "%French")
        assert(b.getName() == "*English")
        assert(b.getSortableName() == "ENGLISH") {
            print("Actual was ${b.getSortableName()}")
        }
        var l = Locale("fr")
        Locale.setDefault(l)
        assert(b.getName() == "%French")
        assert(b.getSortableName() == "FRENCH") {
            print("Actual was ${b.getSortableName()}")
        }
        l = Locale("es")
        Locale.setDefault(l)
        assert(b.getName() == "*English")
        assert(b.getSortableName() == "ENGLISH") {
            print("Actual was ${b.getSortableName()}")
        }
    }
}