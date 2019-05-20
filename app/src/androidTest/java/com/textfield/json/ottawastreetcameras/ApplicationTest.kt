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
        json.put("descriptionFr", "nameFr")
        json.put("description", "name")
        json.put("number", 100)
        json.put("type", "MTO")
        json.put("id", 0)
        json.put("latitude", 45.451235)
        json.put("longitude", -75.6742136)

        val c = Camera(json)
        c.writeToParcel(par, 0)
        par.setDataPosition(0)
        val b = Camera(par)
        assertEquals(c, b)
    }
}