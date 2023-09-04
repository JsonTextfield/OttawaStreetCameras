package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.Neighbourhood
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NeighbourhoodTest {
    private lateinit var neighbourhood: Neighbourhood

    @Before
    fun setup() {
        neighbourhood = Neighbourhood(JSONObject().apply {
            put("geometry", JSONObject().apply {
                put("type", "Polygon")
                put(
                    "coordinates",
                    JSONArray().apply {
                        put(
                            JSONArray().apply {
                                put(JSONArray().apply {
                                    put(-70)
                                    put(40)
                                })
                                put(JSONArray().apply {
                                    put(-70)
                                    put(46)
                                })
                                put(JSONArray().apply {
                                    put(-76)
                                    put(46)
                                })
                                put(JSONArray().apply {
                                    put(-76)
                                    put(40)
                                })
                                put(JSONArray().apply {
                                    put(-70)
                                    put(40)
                                })
                            }
                        )
                    }
                )
            })
            put("properties", JSONObject())
        })
    }

    @Test
    fun testContainsCameraInside() {
        val camera = Camera(JSONObject().apply {
            put("latitude", 45)
            put("longitude", -75)
        })

        assertTrue(neighbourhood.containsCamera(camera))
    }

    @Test
    fun testContainsCameraOutside() {
        val camera = Camera(JSONObject().apply {
            put("latitude", 60)
            put("longitude", -75)
        })

        assertFalse(neighbourhood.containsCamera(camera))
    }

    @Test
    fun testContainsCameraOnLine() {
        val camera = Camera(JSONObject().apply {
            put("latitude", 45)
            put("longitude", -72)
        })

        assertTrue(neighbourhood.containsCamera(camera))
    }

    @Test
    fun testContainsCameraOnVerticalLine() {
        val camera = Camera(JSONObject().apply {
            put("latitude", 44)
            put("longitude", -76)
        })

        assertTrue(neighbourhood.containsCamera(camera))
    }


}