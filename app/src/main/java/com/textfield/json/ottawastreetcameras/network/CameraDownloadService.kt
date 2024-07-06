package com.textfield.json.ottawastreetcameras.network

import android.content.Context
import com.textfield.json.ottawastreetcameras.entities.Camera

interface CameraDownloadService {
    fun download(
        context: Context,
        onError: () -> Unit = {},
        onComplete: (data: List<Camera>) -> Unit = {},
    )
}