package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ui.components.menu.Action
import com.textfield.json.ottawastreetcameras.ui.components.menu.ActionBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    mainViewModel: MainViewModel,
    actions: List<Action> = emptyList(),
    onTitleClicked: () -> Unit = {},
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
            AppBarTitle(
                cameraState,
                onClick = onTitleClicked,
                suggestions = mainViewModel.suggestionList,
                searchText = mainViewModel.searchText,
                onTextChanged = { mainViewModel.searchCameras(cameraState.searchMode, it) },
            )
        },
        actions = {
            val screenWidthDp =
                (LocalWindowInfo.current.containerSize.width / LocalDensity.current.density).toInt()
            val maxActions = when {
                screenWidthDp < 400 -> screenWidthDp / 4 / 48
                screenWidthDp < 600 -> screenWidthDp / 3 / 48
                screenWidthDp < 800 -> screenWidthDp / 2 / 48
                else -> screenWidthDp * 2 / 3 / 48
            } + 1
            ActionBar(
                maxActions = maxActions,
                actions = actions,
            )
        },
    )
}