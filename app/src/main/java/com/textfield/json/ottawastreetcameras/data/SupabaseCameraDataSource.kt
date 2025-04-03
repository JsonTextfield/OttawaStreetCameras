package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.CameraApiModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class SupabaseCameraDataSource @Inject constructor(private val supabaseClient: SupabaseClient) :
    ICameraDataSource {

    override suspend fun getAllCameras(): List<Camera> {
        return supabaseClient.from("cameras").select {
            filter {
                eq("city", "ottawa")
            }
        }.decodeList<CameraApiModel>().map {
            it.toCamera()
        }
    }
}