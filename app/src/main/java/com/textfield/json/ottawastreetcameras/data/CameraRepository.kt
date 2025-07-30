package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.CameraApiModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class CameraRepository(
    private val supabaseClient: SupabaseClient
) : ICameraRepository {
    private var allCameras: List<Camera> = emptyList()
    override suspend fun getAllCameras(): List<Camera> {
        if (allCameras.isEmpty()) {
            allCameras = supabaseClient.from("cameras").select {
                filter {
                    eq("city", "ottawa")
                }
            }.decodeList<CameraApiModel>().map {
                it.toCamera()
            }
        }
        return allCameras
    }
}