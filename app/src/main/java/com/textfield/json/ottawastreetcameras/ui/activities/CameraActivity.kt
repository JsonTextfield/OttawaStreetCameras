package com.textfield.json.ottawastreetcameras.ui.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.screens.CameraScreen
import com.textfield.json.ottawastreetcameras.ui.theme.AppTheme
import com.textfield.json.ottawastreetcameras.ui.viewmodels.CameraViewModel

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val shuffle = intent.getBooleanExtra("shuffle", false)

        var cameras = ArrayList<Camera>()
        var displayedCameras = ArrayList<Camera>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameras = intent.getParcelableArrayListExtra("cameras", Camera::class.java) ?: cameras
            displayedCameras =
                intent.getParcelableArrayListExtra("displayedCameras", Camera::class.java) ?: displayedCameras
        }
        else {
            cameras = intent.getParcelableArrayListExtra("cameras") ?: cameras
            displayedCameras = intent.getParcelableArrayListExtra("displayedCameras") ?: displayedCameras
        }

        val cameraViewModel = CameraViewModel(cameras, displayedCameras, shuffle)
        setContent { AppTheme { CameraScreen(cameraViewModel) } }
    }
}