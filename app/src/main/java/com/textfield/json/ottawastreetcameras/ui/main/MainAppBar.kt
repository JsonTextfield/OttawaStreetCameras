package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.components.menu.ActionBar
import com.textfield.json.ottawastreetcameras.ui.components.menu.getActions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    mainViewModel: MainViewModel,
    listState: LazyListState,
    gridState: LazyGridState,
    snackbarHostState: SnackbarHostState,
    onNavigateToCameraScreen: (List<Camera>, Boolean) -> Unit = { _, _ -> },
) {
    val cameraState by mainViewModel.cameraState.collectAsStateWithLifecycle()
    TopAppBar(
        modifier = Modifier.shadow(10.dp),
        navigationIcon = {
            if (cameraState.showBackButton) {
                IconButton(onClick = mainViewModel::resetFilters) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        stringResource(id = R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        title = {
            val scope = rememberCoroutineScope()
            AppBarTitle(
                cameraState,
                onClick = {
                    scope.launch {
                        when (cameraState.viewMode) {
                            ViewMode.LIST -> listState.scrollToItem(0)
                            ViewMode.GALLERY -> gridState.scrollToItem(0)
                            else -> {}
                        }
                    }
                },
                suggestions = mainViewModel.suggestionList,
                searchText = mainViewModel.searchText,
                onTextChanged = { mainViewModel.searchCameras(cameraState.searchMode, it) },
            )
        },
        actions = {
            ActionBar(getActions(mainViewModel, snackbarHostState, onNavigateToCameraScreen))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    )
}