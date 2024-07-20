package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.textfield.json.ottawastreetcameras.ui.components.ErrorScreen
import com.textfield.json.ottawastreetcameras.ui.components.LoadingScreen
import com.textfield.json.ottawastreetcameras.ui.viewmodels.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.viewmodels.UIState

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel<MainViewModel>(
        factory = MainViewModel.MainViewModelFactory,
    )
) {
    val cameraState by mainViewModel.cameraState.collectAsState()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            if (cameraState.uiState == UIState.LOADED) {
                MainAppBar(mainViewModel, listState, gridState, snackbarHostState)
            }
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (cameraState.uiState) {
                UIState.INITIAL -> LaunchedEffect(Unit) { mainViewModel.getAllCameras() }
                UIState.LOADING -> LoadingScreen()
                UIState.LOADED -> MainContent(
                    mainViewModel,
                    listState,
                    gridState,
                    snackbarHostState
                )

                UIState.ERROR -> ErrorScreen { mainViewModel.getAllCameras() }
            }
        }
    }
}