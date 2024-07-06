package com.textfield.json.ottawastreetcameras

import android.os.Parcel
import androidx.test.filters.SmallTest
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.CameraApiModel
import com.textfield.json.ottawastreetcameras.entities.LocationApiModel
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
        val cameraApiModel = CameraApiModel(
            id = "0",
            nameEn = "nameEn",
            nameFr = "nameFr",
            location = LocationApiModel(
                lat = 45.451235,
                lon = -75.6742136,
            ),
        )

        val c = cameraApiModel.toCamera()
        c.writeToParcel(par, 0)
        par.setDataPosition(0)
        val b = Camera(par)
        assertEquals(c, b)
    }
}