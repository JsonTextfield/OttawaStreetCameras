package com.jsontextfield.shared

import com.jsontextfield.shared.entities.Camera
import kotlin.test.Test
import kotlin.test.assertEquals

class CameraTest {

    @Test
    fun getDistanceString() {
        val camera = Camera(distance = -1)
        assertEquals("", camera.distanceString)
    }

    @Test
    fun getDistanceString_0() {
        val camera = Camera(distance = 0)
        assertEquals("0\nm", camera.distanceString)
    }

    @Test
    fun getDistanceString_100() {
        val camera = Camera(distance = 100)
        assertEquals("100\nm", camera.distanceString)
    }

    @Test
    fun getDistanceString_499m() {
        val camera = Camera(distance = 499)
        assertEquals("499\nm", camera.distanceString)
    }

    @Test
    fun getDistanceString_500m() {
        val camera = Camera(distance = 500)
        assertEquals("0.5\nkm", camera.distanceString)
    }

    @Test
    fun getDistanceString_1000() {
        var camera = Camera(distance = 999)
        assertEquals("1.0\nkm", camera.distanceString)

        camera = Camera(distance = 1000)
        assertEquals("1.0\nkm", camera.distanceString)

        camera = Camera(distance = 1001)
        assertEquals("1.0\nkm", camera.distanceString)
    }

    @Test
    fun getDistanceString_8999() {
        val camera = Camera(distance = 8_999_000)
        assertEquals("8999\nkm", camera.distanceString)
    }

    @Test
    fun getDistanceString_9000km() {
        val camera = Camera(distance = 9_000_000)
        assertEquals("9000\nkm", camera.distanceString)
    }

    @Test
    fun getDistanceString_9001km() {
        val camera = Camera(distance = 9_001_000)
        assertEquals(">9000\nkm", camera.distanceString)
    }
}