package com.jsontextfield.core.data

import com.jsontextfield.core.entities.Camera
import com.jsontextfield.core.entities.City
import com.jsontextfield.core.network.model.CameraApiModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class CameraRepository(
    private val supabaseClient: SupabaseClient
) : ICameraRepository {
    private var allCameras: List<Camera> = emptyList()
    override suspend fun getAllCameras(city: City): List<Camera> {
        if (allCameras.isEmpty() || allCameras.first().city != city) {
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