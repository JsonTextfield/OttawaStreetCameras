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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.components.menu.ActionBar
import com.textfield.json.ottawastreetcameras.ui.components.menu.getActions
import com.textfield.json.ottawastreetcameras.ui.viewmodels.ViewMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    val cameraState by mainViewModel.cameraState.collectAsState()
    TopAppBar(
        modifier = Modifier.shadow(10.dp),
        navigationIcon = {
            if (cameraState.showBackButton) {
                IconButton(onClick = { mainViewModel.resetFilters() }) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        stringResource(id = R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        title = {
            AppBarTitle(mainViewModel) {
                CoroutineScope(Dispatchers.Main).launch {
                    when (cameraState.viewMode) {
                        ViewMode.LIST -> listState.scrollToItem(0)
                        ViewMode.GALLERY -> gridState.scrollToItem(0)
                        else -> {}
                    }
                }
            }
        },
        actions = {
            ActionBar(getActions(mainViewModel, snackbarHostState, onNavigateToCameraScreen))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    )
}