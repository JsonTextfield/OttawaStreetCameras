@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.jsontextfield.streetcamstv.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.jsontextfield.core.ui.CameraRoute
import com.jsontextfield.core.ui.HomeRoute
import com.jsontextfield.core.ui.theme.AppTheme
import com.jsontextfield.core.ui.viewmodels.CameraViewModel
import com.jsontextfield.core.ui.viewmodels.MainViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
    val mainViewModel = koinViewModel<MainViewModel>()
    val theme by mainViewModel.theme.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    AppTheme(theme = theme) {
        Surface {
            NavHost(navController, startDestination = HomeRoute) {
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