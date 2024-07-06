package com.textfield.json.ottawastreetcameras.network

import android.graphics.Bitmap

interface CameraDownloadService {
    fun downloadImage(
        url: String,
        onError: () -> Unit = {},
        onComplete: (bitmap: Bitmap) -> Unit = {},
    )
}