package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.City
import com.textfield.json.ottawastreetcameras.network.model.CameraApiModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class CameraRepository(
    private val supabaseClient: SupabaseClient
) : ICameraRepository {
    private var allCameras: List<Camera> = emptyList()
    override suspend fun getAllCameras(city: City): List<Camera> {
        if (allCameras.isEmpty()) {
            allCameras = supabaseClient.from("cameras").select {
                filter {
                    eq("city", city.cityName)
                }
            }.decodeList<CameraApiModel>().map {
                it.toCamera()
            }
        }
        return allCameras
    }
}