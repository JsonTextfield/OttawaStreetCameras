package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
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
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
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
                    val cameraViewModel = koinViewModel<CameraViewModel> {
                        parametersOf(route.cameras, route.isShuffling)
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

@PreviewLightDark
@PreviewDynamicColors
@PreviewFontScale
@PreviewScreenSizes
@Composable
fun AppPreview() {
    App()
}