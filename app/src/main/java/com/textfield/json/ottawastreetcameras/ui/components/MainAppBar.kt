package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ViewMode
import com.textfield.json.ottawastreetcameras.ui.components.menu.ActionBar
import com.textfield.json.ottawastreetcameras.ui.components.menu.getActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(cameraViewModel: CameraViewModel, listState: LazyListState, gridState: LazyGridState) {
    val cameraState by cameraViewModel.cameraState.collectAsState()
    TopAppBar(
        navigationIcon = {
            if (cameraState.showBackButton) {
                IconButton(onClick = { cameraViewModel.resetFilters() }) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        stringResource(id = R.string.back),
                        tint = Color.White
                    )
                }
            }
        },
        title = {
            AppBarTitle(cameraViewModel) {
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
            ActionBar(getActions(cameraViewModel))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (!isSystemInDarkTheme() || cameraState.selectedCameras.isNotEmpty()) {
                colorResource(id = R.color.colorAccent)
            }
            else {
                Color.Black
            }
        ),
    )
}