package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class CameraModelUnitTest {
    private var cameraModel = CameraModel()

    private fun createCamera(): Camera {
        val json = JSONObject()
        json.put("descriptionFr", "nameFr")
        json.put("description", "name")
        json.put("number", 100)
        json.put("type", "Ottawa")
        json.put("id", Random.nextInt(1_000_000))
        json.put("latitude", 45.451235)
        json.put("longitude", -75.6742136)

        return Camera(json)
    }

    @Before
    fun resetCameraModel() {
        cameraModel = CameraModel()
    }

    @Test
    fun testSelectCamera() {
        val json = JSONObject()
        val camera = Camera(json)

        var result = cameraModel.selectCamera(camera)

        assertTrue(result)
        assertEquals(1, cameraModel.selectedCameras.size)

        result = cameraModel.selectCamera(camera)

        assertFalse(result)
        assertEquals(0, cameraModel.selectedCameras.size)
    }

    @Test
    fun testFiltering() {
        val cameraList: List<Camera> = (0 until 10).map { i ->
            val json = JSONObject()
            json.put("description", "Camera$i")
            val camera = Camera(json)
            camera.setFavourite(Random.nextBoolean())
            camera.setVisible(Random.nextBoolean())
            camera
        }
        cameraModel = CameraModel(ArrayList(cameraList))
        assertEquals(cameraList, cameraModel.allCameras)

        val visibleCameras = cameraList.filter { it.isVisible }
        val hiddenCameras = cameraList.filter { !it.isVisible }
        val favouriteCameras = cameraList.filter { it.isFavourite }
        val favouriteHiddenCameras = cameraList.filter { it.isFavourite }.filter { !it.isVisible }

        assertEquals(visibleCameras, cameraModel.displayedCameras)

        cameraModel.filterDisplayedCameras { !it.isVisible }
        assertEquals(hiddenCameras, cameraModel.displayedCameras)
        cameraModel.resetDisplayedCameras()

        cameraModel.filterDisplayedCameras { it.isFavourite }
        assertEquals(favouriteCameras, cameraModel.displayedCameras)
        cameraModel.resetDisplayedCameras()

        cameraModel.filterDisplayedCameras { it.isFavourite && !it.isVisible }
        assertEquals(favouriteHiddenCameras, cameraModel.displayedCameras)
        cameraModel.resetDisplayedCameras()

        assertEquals(visibleCameras, cameraModel.displayedCameras)

    }

    @Test
    fun testSortByName() {
        val cameraList: List<Camera> = (100 downTo 0).map { i ->
            val json = JSONObject()
            json.put("description", "Camera$i")
            val camera = Camera(json)
            camera
        }
        cameraModel = CameraModel(ArrayList(cameraList))
        cameraModel.sortByName()

        val stringList: List<String> = (0..100).map { "Camera$it" }.sorted()
        stringList.indices.forEach { index ->
            assertEquals(stringList[index], cameraModel.displayedCameras[index].getName())
        }
    }

    @Test
    fun testSortByDistance() {
        /*val cameras: List<Camera> = (0..20).map {
            val json = JSONObject()
            json.put("description", "Camera$it")
            json.put("latitude", it.toDouble())
            json.put("longitude", it.toDouble())
            Camera(json)
        }
        cameraModel = CameraModel(ArrayList(cameras))
        val locationB = Location("point B")
        val lat = 0.0
        val lon = 0.0

        locationB.latitude = lat
        locationB.longitude = lat
        cameraModel.sortByDistance(lat, lon)

        cameraModel.displayedCameras.map {
            val locationA = Location("")
            locationA.latitude = it.lat
            locationA.longitude = it.lon
            print(locationB.distanceTo(locationA))
            locationB.distanceTo(locationA)
        }*/
    }

    @Test
    fun testSortByNeighbourhood() {

    }
}