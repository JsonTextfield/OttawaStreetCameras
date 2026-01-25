@file:OptIn(ExperimentalMaterial3Api::class)

package com.jsontextfield.composeapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jsontextfield.shared.entities.Camera
import com.jsontextfield.shared.ui.FilterMode
import com.jsontextfield.shared.ui.Status
import com.jsontextfield.shared.ui.components.ErrorScreen
import com.jsontextfield.shared.ui.components.LoadingScreen
import com.jsontextfield.shared.ui.components.menu.Action
import com.jsontextfield.shared.ui.components.menu.getActions
import com.jsontextfield.shared.ui.main.CameraState
import com.jsontextfield.shared.ui.main.MainAppBar
import com.jsontextfield.shared.ui.main.MainContent
import com.jsontextfield.shared.ui.viewmodels.MainViewModel
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import streetcams.composeapp.generated.resources.Res
import streetcams.composeapp.generated.resources.round_arrow_upward_24

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = koinViewModel<MainViewModel>(),
    onNavigateToCameraScreen: (List<Camera>, Boolean) -> Unit = { _, _ -> },
    onNavigateToCitySelectionScreen: () -> Unit = {},
) {
    val cameraState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val actions = getActions(
        mainViewModel,
        snackbarHostState,
        onNavigateToCameraScreen
    )

    MainScreen(
        cameraState = cameraState,
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
        onRetry = mainViewModel::reloadData,
        onNavigateToCameraScreen = onNavigateToCameraScreen,
        onHideCameras = mainViewModel::hideCameras,
        onFavouriteCameras = mainViewModel::favouriteCameras,
        onChangeFilterMode = mainViewModel::changeFilterMode,
        onBackClick = mainViewModel::resetFilters,
        onNavigateToCitySelectionScreen = onNavigateToCitySelectionScreen,
    )
}

@Composable
private fun MainScreen(
    cameraState: CameraState,
    gridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
    actions: List<Action> = emptyList(),
    searchText: String = "",
    suggestions: List<String> = emptyList(),
    onSearchTextChanged: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    onSelectCamera: (Camera) -> Unit = {},
    onHideCameras: (List<Camera>) -> Unit = {},
    onFavouriteCameras: (List<Camera>) -> Unit = {},
    onChangeFilterMode: (FilterMode) -> Unit = {},
    onNavigateToCameraScreen: (List<Camera>, Boolean) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {},
    onNavigateToCitySelectionScreen: () -> Unit = {},
) {
    var showUpButton by remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            if (cameraState.status == Status.LOADED) {
                MainAppBar(
                    cameraState = cameraState,
                    searchText = searchText,
                    suggestions = suggestions,
                    actions = actions,
                    onSearchTextChanged = onSearchTextChanged,
                    onBackClick = onBackClick,
                    onNavigateToCitySelectionScreen = onNavigateToCitySelectionScreen
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showUpButton,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier.padding(end = TopAppBarDefaults.windowInsets.asPaddingValues().calculateEndPadding(
                    LayoutDirection.Ltr))
            ) {
                val scope = rememberCoroutineScope()
                FilledIconButton(
                    onClick = {
                        scope.launch {
                            gridState.animateScrollToItem(0)
                        }
                    },
                ) {
                    Icon(painterResource(Res.drawable.round_arrow_upward_24), null)
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            when (cameraState.status) {
                Status.INITIAL,
                Status.LOADING -> LoadingScreen()

                Status.LOADED -> MainContent(
                    cameraState = cameraState,
                    gridState = gridState,
                    searchText = searchText,
                    snackbarHostState = snackbarHostState,
                    onCameraClicked = { camera: Camera ->
                        if (cameraState.selectedCameras.isNotEmpty()) {
                            onSelectCamera(camera)
                        } else {
                            onNavigateToCameraScreen(listOf(camera), false)
                        }
                    },
                    onHideCameras = onHideCameras,
                    onFavouriteCameras = onFavouriteCameras,
                    onCameraLongClick = onSelectCamera,
                    onChangeFilterMode = onChangeFilterMode,
                )

                Status.ERROR -> ErrorScreen(retry = onRetry)
            }
            LaunchedEffect(gridState) {
                snapshotFlow { gridState.layoutInfo.visibleItemsInfo.firstOrNull() }
                    .mapNotNull { it?.index }.collect { index ->
                        showUpButton = index > 4
                    }
            }
        }
    }
}