package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class CameraUnitTest {
    private var json = JSONObject()
    @Before
    fun before() {
        json = JSONObject()
    }

    @Test
    fun test1() {
        json.put("descriptionFr", "nameFr")
        json.put("description", "name")
        json.put("number", 100)
        json.put("type", "Ottawa")
        json.put("id", 0)
        json.put("latitude", 45.451235)
        json.put("longitude", -75.6742136)

        val c = Camera(json)

        assertEquals(json["latitude"], c.lat)
        assertEquals(json["longitude"], c.lng)
        assertEquals(json["number"], c.num)
        assertEquals(json["type"], c.owner)
    }

    //Testing owner is MTO
    @Test
    fun test2() {
        val number = 100
        json.put("descriptionFr", "nameFr")
        json.put("description", "name")
        json.put("number", number)
        json.put("type", "MTO")
        json.put("id", 0)
        json.put("latitude", 45.451235)
        json.put("longitude", -75.6742136)

        val c = Camera(json)
        assertEquals("MTO", c.owner)
        assertEquals(number + 2000, c.num)
    }

    // Testing sortable name
    @Test
    fun test3() {
        val name = "name"
        val nameFr = "nameFr"
        json.put("descriptionFr", nameFr)
        json.put("description", name)
        json.put("number", 100)
        json.put("type", "MTO")
        json.put("id", 0)
        json.put("latitude", 45.451235)
        json.put("longitude", -75.6742136)

        val c = Camera(json)
        assertEquals(c.getSortableName(), name.uppercase(Locale.getDefault()))
        val l = Locale("fr")
        Locale.setDefault(l)
        assertEquals(c.getSortableName(), nameFr.uppercase(Locale.getDefault()))
    }

    //Testing language-based name
    @Test
    fun test4() {
        json.put("descriptionFr", "nameFr")
        json.put("description", "name")
        json.put("number", 100)
        json.put("type", "MTO")
        json.put("id", 0)
        json.put("latitude", 45.451235)
        json.put("longitude", -75.6742136)

        val c = Camera(json)
        assertEquals(c.getName(), "name")
        var l = Locale("fr")
        Locale.setDefault(l)
        assertEquals(c.getName(), "nameFr")
        l = Locale("es")
        Locale.setDefault(l)
        assertEquals(c.getName(), "name")
    }

    @After
    fun clear() {
        json = JSONObject()
        var l = Locale("en")
        Locale.setDefault(l)
    }
}