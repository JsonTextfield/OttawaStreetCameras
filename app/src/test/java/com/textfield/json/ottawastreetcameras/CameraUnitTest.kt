package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class CameraUnitTest {
    private var json = JSONObject()

    @Before
    fun before() {
        json = JSONObject()
        val location = JSONObject()
        location.put("lat", 45.451235)
        location.put("lon", -75.6742136)
        json.put("location", location)
    }

    @Test
    fun testJSON() {
        json.put("nameEn", "nameEn")
        json.put("nameFr", "nameFr")
        json.put("lat", 45.451235)
        json.put("lon", -75.6742136)

        val camera = Camera.fromJson(json)

        Locale.setDefault(Locale("en"))
        assertEquals(json["nameEn"], camera.name)

        Locale.setDefault(Locale("fr"))
        assertEquals(json["nameFr"], camera.name)

        assertEquals(json["lat"], camera.lat)
        assertEquals(json["lon"], camera.lon)
    }

    @Test
    fun testEquality() {
        json.put("id", "1337")
        val camera1 = Camera.fromJson(json)

        json = JSONObject()
        json.put("id", "1337")
        val location = JSONObject()
        location.put("lat", 45.451235)
        location.put("lon", -75.6742136)
        json.put("location", location)

        val camera2 = Camera.fromJson(json)

        assertEquals(camera1, camera2)
    }

    @Test
    fun testEqualityDifferentName() {
        json.put("nameEn", "Camera 1")
        json.put("id", "1337")
        val camera1 = Camera.fromJson(json)

        json = JSONObject()
        json.put("nameEn", "Camera 2")
        json.put("id", "1337")
        val location = JSONObject()
        location.put("lat", 45.451235)
        location.put("lon", -75.6742136)
        json.put("location", location)
        val camera2 = Camera.fromJson(json)

        assertEquals(camera1, camera2)
    }

    @Test
    fun testInequalityId() {
        json.put("id", "1337")
        val camera1 = Camera.fromJson(json)

        json = JSONObject()
        json.put("id", "1331")
        val location = JSONObject()
        location.put("lat", 45.451235)
        location.put("lon", -75.6742136)
        json.put("location", location)
        val camera2 = Camera.fromJson(json)

        assertNotEquals(camera1, camera2)
    }

    @After
    fun after() {
        Locale.setDefault(Locale("en"))
    }
}