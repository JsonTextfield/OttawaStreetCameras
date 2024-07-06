package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsApp
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.entities.CameraApiModel
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupabaseCameraDataSource : CameraDataSource {
    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://nacudfxzbqaesoyjfluh.supabase.co",
            supabaseKey = StreetCamsApp.resources.getString(R.string.supabase_key)
        ) {
            install(Postgrest)
        }
    }

    override suspend fun getAllCameras(): Flow<List<Camera>> {
        return flow {
            emit(
                supabase.from("cameras").select {
                    filter {
                        eq("city", "ottawa")
                    }
                }.decodeList<CameraApiModel>().map {
                    it.toCamera()
                }
            )
        }
    }
}