package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class CameraModelTest {
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
        val cameraList: List<Camera> = (0 until 100).map { i ->
            val json = JSONObject()
            json.put("description", "Camera$i")
            val camera = Camera(json)
            camera.setFavourite(Random.nextBoolean())
            camera.setVisible(Random.nextBoolean())
            camera
        }
        cameraModel = CameraModel(ArrayList(cameraList))
        val visibleCameras = cameraList.filter { it.isVisible }
        val hiddenCameras = cameraList.filter { !it.isVisible }
        val favouriteCameras = cameraList.filter { it.isFavourite }

        cameraModel
            .filterDisplayedCameras { it.isFavourite }

        assertEquals(visibleCameras, cameraModel.displayedCameras)

    }

    @Test
    fun testSortByName() {

    }

    @Test
    fun testSortByDistance() {

    }

    @Test
    fun testSortByNeighbourhood() {

    }
}