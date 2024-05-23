package com.textfield.json.ottawastreetcameras.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.UIState
import com.textfield.json.ottawastreetcameras.ui.components.ErrorScreen
import com.textfield.json.ottawastreetcameras.ui.components.LoadingScreen
import com.textfield.json.ottawastreetcameras.ui.components.MainAppBar
import com.textfield.json.ottawastreetcameras.ui.components.MainContent
import com.textfield.json.ottawastreetcameras.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val cameraViewModel: CameraViewModel by viewModels { CameraViewModel.CameraViewModelFactory }
                val cameraState by cameraViewModel.cameraState.collectAsState()
                val listState = rememberLazyListState()
                val gridState = rememberLazyGridState()
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                    topBar = {
                        if (cameraState.uiState == UIState.LOADED) {
                            MainAppBar(cameraViewModel, listState, gridState, snackbarHostState)
                        }
                    },
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        val context = LocalContext.current
                        when (cameraState.uiState) {
                            UIState.INITIAL -> LaunchedEffect(true) { cameraViewModel.download(context) }
                            UIState.LOADING -> LoadingScreen()
                            UIState.LOADED -> MainContent(cameraViewModel, listState, gridState, snackbarHostState)
                            UIState.ERROR -> ErrorScreen { cameraViewModel.download(context) }
                        }
                    }
                }
            }
        }
    }
}