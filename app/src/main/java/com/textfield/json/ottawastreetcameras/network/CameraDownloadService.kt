package com.textfield.json.ottawastreetcameras.network

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface CameraDownloadService {
    suspend fun downloadImage(url: String): Flow<Bitmap?>
}