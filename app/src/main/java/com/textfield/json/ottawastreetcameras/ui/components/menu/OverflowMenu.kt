package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun OverflowMenu(
    expanded: Boolean,
    actions: List<Action>,
    showViewMode: Boolean = false,
    showSortModeMenu: Boolean = false,
    onItemSelected: () -> Unit,
) {
    val cameraManager = CameraManager.getInstance()
    val cameraState by cameraManager.cameraState.collectAsState()
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onItemSelected() },
    ) {
        if (showViewMode) {
            Box {
                var showViewModeMenu by remember { mutableStateOf(false) }
                ViewModeMenu(showViewModeMenu) {
                    showViewModeMenu = false
                    onItemSelected()
                }
                OverflowMenuItem(
                    icon = when (cameraState.viewMode) {
                        ViewMode.LIST -> Icons.Rounded.List
                        ViewMode.MAP -> Icons.Filled.Place
                        else -> Icons.Rounded.GridView
                    },
                    tooltip = when (cameraState.viewMode) {
                        ViewMode.LIST -> stringResource(R.string.list)
                        ViewMode.MAP -> stringResource(R.string.map)
                        else -> stringResource(R.string.gallery)
                    },
                    visible = true
                ) {
                    showViewModeMenu = true
                }
            }
        }
        if (showSortModeMenu) {
            Box {
                var showSortMenu by remember { mutableStateOf(false) }
                SortModeMenu(showSortMenu) {
                    showSortMenu = false
                    onItemSelected()
                }

                OverflowMenuItem(
                    icon = Icons.Rounded.Sort,
                    tooltip = stringResource(R.string.sort),
                    visible = cameraState.viewMode != ViewMode.MAP
                ) {
                    showSortMenu = !showSortMenu
                }
            }
        }
        for (action in actions) {
            OverflowMenuItem(
                icon = action.icon,
                tooltip = action.toolTip,
                visible = action.condition,
                checked = action.checked,
            ) {
                action.onClick()
                onItemSelected()
            }
        }
    }
}