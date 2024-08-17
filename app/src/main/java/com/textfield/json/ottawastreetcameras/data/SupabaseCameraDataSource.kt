package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsApp
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.CameraApiModel
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from

class SupabaseCameraDataSource : CameraDataSource {
    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://nacudfxzbqaesoyjfluh.supabase.co",
            supabaseKey = StreetCamsApp.resources.getString(R.string.supabase_key)
        ) {
            install(Postgrest)
        }
    }

    override suspend fun getAllCameras(): List<Camera> {
        return supabase.from("cameras").select {
            filter {
                eq("city", "ottawa")
            }
        }.decodeList<CameraApiModel>().map {
            it.toCamera()
        }
    }

    override suspend fun getCameraById(id: String): Camera {
        return supabase.from("cameras").select {
            filter {
                eq("id", id)
            }
        }.decodeAs<CameraApiModel>().toCamera()
    }
}