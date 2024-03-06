package com.textfield.json.ottawastreetcameras

import android.os.Parcel
import androidx.test.filters.SmallTest
import com.textfield.json.ottawastreetcameras.entities.Camera
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 */
@SmallTest
class ApplicationTest {

    @Test
    fun test1() {
        val par = Parcel.obtain()
        val json = JSONObject()
        json.put("nameEn", "nameEn")
        json.put("nameFr", "nameFr")
        json.put("id", 0)
        val loc = JSONObject()
        loc.put("lat", 45.451235)
        loc.put("lon", -75.6742136)
        json.put("location", loc)

        val c = Camera.fromJson(json)
        c.writeToParcel(par, 0)
        par.setDataPosition(0)
        val b = Camera(par)
        assertEquals(c, b)
    }
}