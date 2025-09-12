package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.ui.components.menu.Action
import com.textfield.json.ottawastreetcameras.ui.components.menu.ActionBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    cameraState: CameraState,
    searchText: String = "",
    suggestions: List<String> = emptyList(),
    actions: List<Action> = emptyList(),
    onSearchTextChanged: (String) -> Unit = {},
) {
    TopAppBar(
        modifier = Modifier.shadow(10.dp),
        title = {
            AppBarTitle(
                cameraState,
                suggestions = suggestions,
                searchText = searchText,
                onTextChanged = onSearchTextChanged,
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