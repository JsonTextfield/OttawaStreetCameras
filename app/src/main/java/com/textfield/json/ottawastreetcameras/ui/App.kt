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
import com.jsontextfield.core.ui.CameraRoute
import com.jsontextfield.core.ui.CitySelectionRoute
import com.jsontextfield.core.ui.CitySelectionScreen
import com.jsontextfield.core.ui.HomeRoute
import com.jsontextfield.core.ui.theme.AppTheme
import com.jsontextfield.core.ui.viewmodels.CameraViewModel
import com.jsontextfield.core.ui.viewmodels.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.screens.CameraScreen
import com.textfield.json.ottawastreetcameras.ui.screens.MainScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
    val mainViewModel = koinViewModel<MainViewModel>()
    val cameraState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val theme by mainViewModel.theme.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    AppTheme(theme = theme) {
        Surface {
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
            ) {
                composable<HomeRoute> {
                    MainScreen(
                        mainViewModel = mainViewModel,
                        onNavigateToCameraScreen = { selectedCameras, isShuffling ->
                            navController.navigate(
                                CameraRoute(
                                    cameras = selectedCameras.joinToString(",") { it.id },
                                    isShuffling = isShuffling,
                                )
                            )
                        },
                        onNavigateToCitySelectionScreen = {
                            navController.navigate(CitySelectionRoute(cameraState.currentCity))
                        })
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

                composable<CitySelectionRoute>(
                    enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
                ) {
                    val selectedCity = it.toRoute<CitySelectionRoute>().selectedCity
                    CitySelectionScreen(
                        selectedCity = selectedCity,
                        onCitySelected = { city ->
                            mainViewModel.changeCity(city)
                            navController.popBackStack()
                        },
                        onBackPressed = {
                            navController.popBackStack()
                        },
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