package com.textfield.json.ottawastreetcameras.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.textfield.json.ottawastreetcameras.ui.camera.CameraScreen
import com.textfield.json.ottawastreetcameras.ui.camera.CameraViewModel
import com.textfield.json.ottawastreetcameras.ui.main.MainScreen
import com.textfield.json.ottawastreetcameras.ui.main.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel = koinViewModel<MainViewModel>()
            val theme by mainViewModel.theme.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            AppTheme(theme = theme) {
                Surface {
                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute,
                    ) {
                        composable<HomeRoute> {
                            MainScreen(mainViewModel = mainViewModel) { selectedCameras, isShuffling ->
                                navController.navigate(
                                    CameraRoute(
                                        cameras = selectedCameras.joinToString(",") { it.id },
                                        isShuffling = isShuffling,
                                    )
                                )
                            }
                        }
                        composable<CameraRoute>(
                            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
                        ) {
                            val route = it.toRoute<CameraRoute>()
                            val cameraViewModel = koinViewModel<CameraViewModel>()
                            LaunchedEffect(Unit) {
                                if (route.isShuffling) {
                                    cameraViewModel.getRandomCamera()
                                }
                                else {
                                    cameraViewModel.getCameras(route.cameras)
                                }
                            }
                            CameraScreen(
                                isShuffling = route.isShuffling,
                                cameraViewModel = cameraViewModel,
                                onBackPressed = navController::navigateUp,
                            )
                        }
                    }
                }
            }
        }
    }
}