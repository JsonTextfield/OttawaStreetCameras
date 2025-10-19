package com.jsontextfield.core.data

import com.jsontextfield.core.entities.Camera

interface ICameraRepository {
    suspend fun getAllCameras(): List<Camera>
}