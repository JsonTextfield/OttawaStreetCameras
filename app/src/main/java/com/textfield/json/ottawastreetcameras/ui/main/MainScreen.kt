package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowUp
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.components.ErrorScreen
import com.textfield.json.ottawastreetcameras.ui.components.LoadingScreen
import com.textfield.json.ottawastreetcameras.ui.components.menu.Action
import com.textfield.json.ottawastreetcameras.ui.components.menu.getActions
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel<MainViewModel>(),
    onNavigateToCameraScreen: (List<Camera>, Boolean) -> Unit = { _, _ -> },
) {
    val cameraState by mainViewModel.cameraState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val actions = getActions(
        mainViewModel,
        snackbarHostState,
        onNavigateToCameraScreen
    )

    MainScreen(
        cameraState = cameraState,
        listState = listState,
        gridState = gridState,
        snackbarHostState = snackbarHostState,
        actions = actions,
        searchText = mainViewModel.searchText,
        suggestions = mainViewModel.suggestionList,
        onSearchTextChanged = {
            mainViewModel.searchCameras(
                cameraState.searchMode,
                it
            )
        },
        onSelectCamera = mainViewModel::selectCamera,
        onRetry = mainViewModel::getAllCameras,
        onBackPressed = mainViewModel::resetFilters,
        onNavigateToCameraScreen = onNavigateToCameraScreen,
        onHideCameras = mainViewModel::hideCameras,
        onFavouriteCameras = mainViewModel::favouriteCameras
    )
}

@Composable
private fun MainScreen(
    cameraState: CameraState,
    listState: LazyListState,
    gridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
    actions: List<Action> = emptyList(),
    searchText: String = "",
    suggestions: List<String> = emptyList(),
    onSearchTextChanged: (String) -> Unit = {},
    onBackPressed: () -> Unit = {},
    onRetry: () -> Unit = {},
    onSelectCamera: (Camera) -> Unit = {},
    onHideCameras: (List<Camera>) -> Unit = {},
    onFavouriteCameras: (List<Camera>) -> Unit = {},
    onNavigateToCameraScreen: (List<Camera>, Boolean) -> Unit = { _, _ -> },
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            if (cameraState.uiState == UIState.LOADED) {
                val scope = rememberCoroutineScope()
                MainAppBar(
                    cameraState = cameraState,
                    searchText = searchText,
                    suggestions = suggestions,
                    actions = actions,
                    onSearchTextChanged = onSearchTextChanged,
                    onTitleClicked = {
                        scope.launch {
                            if (cameraState.viewMode == ViewMode.LIST) {
                                listState.scrollToItem(0)
                            }
                            else if (cameraState.viewMode == ViewMode.GALLERY) {
                                gridState.scrollToItem(0)
                            }
                        }
                    },
                    onBackPressed = onBackPressed,
                )
            }
        },
    ) {
        Box(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            when (cameraState.uiState) {
                UIState.INITIAL,
                UIState.LOADING -> LoadingScreen()

                UIState.LOADED -> MainContent(
                    cameraState = cameraState,
                    listState = listState,
                    gridState = gridState,
                    searchText = searchText,
                    snackbarHostState = snackbarHostState,
                    onCameraClicked = { camera: Camera ->
                        if (cameraState.selectedCameras.isNotEmpty()) {
                            onSelectCamera(camera)
                        }
                        else {
                            onNavigateToCameraScreen(listOf(camera), false)
                        }
                    },
                    onHideCameras = onHideCameras,
                    onFavouriteCameras = onFavouriteCameras,
                    onCameraLongClick = onSelectCamera,
                )

                UIState.ERROR -> ErrorScreen(retry = onRetry)
            }
            var showUpButton by remember { mutableStateOf(false) }
            if (cameraState.viewMode == ViewMode.LIST) {
                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo.firstOrNull() }
                        .mapNotNull { it?.index }.collect { index ->
                            showUpButton = index > 4
                        }
                }
            }
            else if (cameraState.viewMode == ViewMode.GALLERY) {
                LaunchedEffect(gridState) {
                    snapshotFlow { gridState.layoutInfo.visibleItemsInfo.firstOrNull() }
                        .mapNotNull { it?.index }.collect { index ->
                            showUpButton = index > 4
                        }
                }
            }
            AnimatedVisibility(
                visible = showUpButton,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter).padding(WindowInsets.safeGestures.asPaddingValues())
            ) {
                val scope = rememberCoroutineScope()
                FilledIconButton(
                    onClick = {
                        scope.launch {
                            if (cameraState.viewMode == ViewMode.LIST) {
                                listState.scrollToItem(0)
                            }
                            else if (cameraState.viewMode == ViewMode.GALLERY) {
                                gridState.scrollToItem(0)
                            }
                        }
                    },
                ) {
                    Icon(Icons.Rounded.KeyboardDoubleArrowUp, null)
                }
            }
        }
    }
}