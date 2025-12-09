@file:OptIn(ExperimentalMaterial3Api::class)

package com.textfield.json.ottawastreetcameras.widget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.textfield.json.ottawastreetcameras.ui.components.menu.ActionBar
import com.textfield.json.ottawastreetcameras.ui.components.menu.getActions
import com.textfield.json.ottawastreetcameras.ui.main.AppBarTitle
import com.textfield.json.ottawastreetcameras.ui.main.CameraGalleryTile
import com.textfield.json.ottawastreetcameras.ui.main.CameraListItem
import com.textfield.json.ottawastreetcameras.ui.main.CameraMapView
import com.textfield.json.ottawastreetcameras.ui.main.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import com.textfield.json.ottawastreetcameras.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

class WidgetConfigActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel = koinViewModel<MainViewModel>()
            val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(title = {
                        AppBarTitle(uiState)
                    }, actions = {
                        ActionBar(
                            maxActions = 3, actions = getActions(
                                mainViewModel,
                                snackbarHostState = SnackbarHostState(),
                                onNavigateToCameraScreen = { _, _ -> },
                            )
                        )
                    })
                }) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        when (uiState.viewMode) {
                            ViewMode.LIST -> {
                                LazyVerticalGrid(columns = GridCells.Adaptive(240.dp)) {
                                    items(uiState.allCameras) { camera ->
                                        CameraListItem(camera)
                                    }
                                }
                            }

                            ViewMode.GALLERY -> {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(120.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(all = 8.dp)
                                ) {
                                    items(uiState.allCameras) { camera ->
                                        CameraGalleryTile(camera)
                                    }
                                }
                            }

                            else -> {
                                CameraMapView(searchText = "", cameraState = uiState)
                            }
                        }
                    }
                }
            }
        }
    }
}
