package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
    fun testJSON() {
        json.put("description", "name")
        json.put("descriptionFr", "nameFr")
        json.put("number", 100)
        json.put("type", "Ottawa")
        json.put("latitude", 45.451235)
        json.put("longitude", -75.6742136)

        val camera = Camera(json)

        Locale.setDefault(Locale("en"))
        assertEquals(json["description"], camera.getName())
        Locale.setDefault(Locale("fr"))
        assertEquals(json["descriptionFr"], camera.getName())
        assertEquals(json["number"], camera.num)
        assertEquals(json["type"], camera.owner)
        assertEquals(json["latitude"], camera.lat)
        assertEquals(json["longitude"], camera.lng)
    }

    @Test
    fun testSortableName() {
        val name = "name"
        val nameFr = "nameFr"
        json.put("descriptionFr", nameFr)
        json.put("description", name)

        val camera = Camera(json)

        Locale.setDefault(Locale("en"))
        assertEquals(camera.getSortableName(), name.uppercase(Locale.getDefault()))

        Locale.setDefault(Locale("fr"))
        assertEquals(camera.getSortableName(), nameFr.uppercase(Locale.getDefault()))
    }

    @Test
    fun testSortableNameSpecialCharacters() {
        val name = "!@#\$%^&*()n!@#\$%^&*()a!@#\$%^&*()m!@#\$%^&*()e!@#\$%^&*()"
        val nameFr = "n!@#\$%^&*()a!@#\$%^&*()m!@#\$%^&*()e!@#\$%^&*()F!@#\$%^&*()r!@#\$%^&*()"
        json.put("descriptionFr", nameFr)
        json.put("description", name)

        val camera = Camera(json)

        Locale.setDefault(Locale("en"))
        assertEquals(camera.getSortableName(), name.uppercase(Locale.getDefault()).replace(Regex("\\W"), ""))

        Locale.setDefault(Locale("fr"))
        assertEquals(camera.getSortableName(), nameFr.uppercase(Locale.getDefault()).replace(Regex("\\W"), ""))
    }

    @Test
    fun testNameBasedOnLocale() {
        json.put("descriptionFr", "nameFr")
        json.put("description", "name")

        val camera = Camera(json)
        Locale.setDefault(Locale("en"))
        assertEquals(camera.getName(), "name")

        Locale.setDefault(Locale("fr"))
        assertEquals(camera.getName(), "nameFr")

        Locale.setDefault(Locale("es"))
        assertEquals(camera.getName(), "name")
    }

    @Test
    fun testEquality() {
        json.put("id", 1337)
        json.put("number", 133)
        val camera1 = Camera(json)

        json = JSONObject()
        json.put("id", 1337)
        json.put("number", 133)
        val camera2 = Camera(json)

        assertEquals("These cameras should be equal", camera1, camera2)
    }

    @Test
    fun testEqualityDifferentName() {
        json.put("description", "Camera 1")
        json.put("id", 1337)
        json.put("number", 11)
        val camera1 = Camera(json)

        json = JSONObject()
        json.put("description", "Camera 2")
        json.put("id", 1337)
        json.put("number", 11)
        val camera2 = Camera(json)

        assertEquals("These cameras should be equal", camera1, camera2)
    }

    @Test
    fun testInequalityId() {
        json.put("id", 1337)
        json.put("number", 11)
        val camera1 = Camera(json)

        json = JSONObject()
        json.put("id", 1331)
        json.put("number", 11)
        val camera2 = Camera(json)

        assertNotEquals("These cameras should not be equal", camera1, camera2)
    }

    @Test
    fun testInequalityNum() {
        json.put("id", 1337)
        json.put("number", 11)
        val camera1 = Camera(json)

        json = JSONObject()
        json.put("id", 1337)
        json.put("number", 12)
        val camera2 = Camera(json)

        assertNotEquals("These cameras should not be equal", camera1, camera2)
    }

    @After
    fun after() {
        Locale.setDefault(Locale("en"))
    }
}