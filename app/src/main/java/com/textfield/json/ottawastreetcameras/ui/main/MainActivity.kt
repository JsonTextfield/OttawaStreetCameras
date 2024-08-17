package com.textfield.json.ottawastreetcameras.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.textfield.json.ottawastreetcameras.ui.camera.CameraScreen
import com.textfield.json.ottawastreetcameras.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen { selectedCameras, isShuffling ->
                            navController.navigate("cameras/${selectedCameras.joinToString(",") { it.id }}/$isShuffling")
                        }
                    }
                    composable(
                        "cameras/{ids}/{isShuffling}",
                        arguments = listOf(
                            navArgument("ids") { type = NavType.StringType },
                            navArgument("isShuffling") { type = NavType.BoolType },
                        )
                    ) {
                        CameraScreen(
                            ids = it.arguments?.getString("ids") ?: "",
                            isShuffling = it.arguments?.getBoolean("isShuffling") ?: false,
                        )
                    }
                }
            }
        }
    }
}