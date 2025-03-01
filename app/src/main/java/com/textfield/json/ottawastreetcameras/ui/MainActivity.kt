package com.textfield.json.ottawastreetcameras.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.textfield.json.ottawastreetcameras.ui.camera.CameraScreen
import com.textfield.json.ottawastreetcameras.ui.camera.CameraViewModel
import com.textfield.json.ottawastreetcameras.ui.main.MainScreen
import com.textfield.json.ottawastreetcameras.ui.main.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel = hiltViewModel<MainViewModel>()
            val theme by mainViewModel.theme.collectAsStateWithLifecycle()
            AppTheme(theme = theme) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "main",
                ) {
                    composable("main") {
                        MainScreen(mainViewModel = mainViewModel) { selectedCameras, isShuffling ->
                            navController.navigate("cameras?ids=${selectedCameras.joinToString(",") { it.id }}?isShuffling=$isShuffling")
                        }
                    }
                    composable(
                        "cameras?ids={ids}?isShuffling={isShuffling}",
                        arguments = listOf(
                            navArgument("ids") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = ""
                            },
                            navArgument("isShuffling") {
                                type = NavType.BoolType
                                defaultValue = false
                            },
                        ),
                        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                        exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
                    ) {
                        val cameraViewModel = hiltViewModel<CameraViewModel>()
                        CameraScreen(
                            cameraViewModel = cameraViewModel,
                            ids = it.arguments?.getString("ids") ?: "",
                            isShuffling = it.arguments?.getBoolean("isShuffling") ?: false,
                            onBackPressed = navController::navigateUp,
                        )
                    }
                }
            }
        }
    }
}