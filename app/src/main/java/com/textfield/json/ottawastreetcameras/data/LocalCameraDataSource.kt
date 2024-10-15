package com.textfield.json.ottawastreetcameras.data

import android.content.Context
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import javax.inject.Inject

class LocalCameraDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : ICameraDataSource {
    override suspend fun getAllCameras(): List<Camera> {
        val inputStream = context.assets.open("cameras.json")
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        val content = String(buffer)
        val jsonArray = JSONArray(content)
        return (0 until jsonArray.length()).map {
            Camera(
                id = jsonArray.getJSONObject(it).optString("id"),
                _name = BilingualObject(
                    en = jsonArray.getJSONObject(it).optString("nameEn"),
                    fr = jsonArray.getJSONObject(it).optString("nameFr"),
                ),
                _neighbourhood = BilingualObject(
                    en = jsonArray.getJSONObject(it).optString("neighbourhoodEn"),
                    fr = jsonArray.getJSONObject(it).optString("neighbourhoodFr"),
                ),
                lat = jsonArray.getJSONObject(it).optDouble("lat"),
                lon = jsonArray.getJSONObject(it).optDouble("lon"),
                _url = jsonArray.getJSONObject(it).optString("url"),
            )
        }
    }
}