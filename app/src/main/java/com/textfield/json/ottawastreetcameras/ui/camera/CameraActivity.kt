package com.textfield.json.ottawastreetcameras.ui.camera

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.theme.AppTheme

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isShuffling = intent.getBooleanExtra("shuffle", false)

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
        enableEdgeToEdge()
        setContent {
            AppTheme {
                CameraScreen(
                    cameras = cameras,
                    displayedCameras = displayedCameras,
                    isShuffling = isShuffling,
                )
            }
        }
    }
}