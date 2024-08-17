package com.textfield.json.ottawastreetcameras.entities

import org.junit.Assert.assertEquals
import org.junit.Test

class CameraTest {

    @Test
    fun getDistanceString() {
        val camera = Camera()
        camera.distance = -1
        assertEquals("", camera.distanceString)
    }

    @Test
    fun getDistanceString_0() {
        val camera = Camera()
        camera.distance = 0
        assertEquals("0\nm", camera.distanceString)
    }

    @Test
    fun getDistanceString_100() {
        val camera = Camera()
        camera.distance = 100
        assertEquals("100\nm", camera.distanceString)
    }

    @Test
    fun getDistanceString_499m() {
        val camera = Camera()
        camera.distance = 499
        assertEquals("499\nm", camera.distanceString)
    }

    @Test
    fun getDistanceString_500m() {
        val camera = Camera()
        camera.distance = 500
        assertEquals("0.5\nkm", camera.distanceString)
    }

    @Test
    fun getDistanceString_1000() {
        val camera = Camera()
        camera.distance = 999
        assertEquals("1.0\nkm", camera.distanceString)

        camera.distance = 1000
        assertEquals("1.0\nkm", camera.distanceString)

        camera.distance = 1001
        assertEquals("1.0\nkm", camera.distanceString)
    }

    @Test
    fun getDistanceString_8999() {
        val camera = Camera()
        camera.distance = 8_999_000
        assertEquals("8999\nkm", camera.distanceString)
    }

    @Test
    fun getDistanceString_9000km() {
        val camera = Camera()
        camera.distance = 9_000_000
        assertEquals("9000\nkm", camera.distanceString)
    }

    @Test
    fun getDistanceString_9001km() {
        val camera = Camera()
        camera.distance = 9_001_000
        assertEquals(">9000\nkm", camera.distanceString)
    }
}