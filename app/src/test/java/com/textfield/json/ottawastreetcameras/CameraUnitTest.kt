package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

class CameraUnitTest {
    private var json = JSONObject()
    @Before
    fun before() {
        println("Setting up")

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

        assert(c.lat == json["latitude"]) {
            print("expected ${json["latitude"]} but got ${c.lat}")
        }
        assert(c.lng == json["longitude"]) {
            print("expected ${json["longitude"]} but got ${c.lng}")
        }
        assert(c.num == json["number"]) {
            print("expected ${json["number"]} but got ${c.num}")
        }

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
        assert(c.owner == "MTO")
        assert(c.num == number + 2000)
    }

    // Testing sortable name
    @Test
    fun test3() {

    }

    //Testing language-based name
    @Test
    fun test4() {

    }

    @After
    fun clear() {
        json = JSONObject()
    }
}