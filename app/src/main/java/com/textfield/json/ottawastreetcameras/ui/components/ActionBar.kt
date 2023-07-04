package com.textfield.json.ottawastreetcameras.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun ActionBar(actions: List<Action>, onItemSelected: () -> Unit) {
    val cameraManager = CameraManager.getInstance()
    val width = LocalConfiguration.current.screenWidthDp
    val maxActions = width / 48 / 3
    var remainingActions = maxActions
    Log.e("WIDTH", width.toString())
    Log.e("MAX_ACTIONS", remainingActions.toString())

    Box {
        var showViewModeMenu by remember { mutableStateOf(false) }
        ViewModeMenu(showViewModeMenu) {
            showViewModeMenu = false
            onItemSelected()
        }
        MenuItem(
            icon = when (cameraManager.viewMode.value) {
                ViewMode.LIST -> Icons.Rounded.List
                ViewMode.MAP -> Icons.Filled.Place
                else -> Icons.Rounded.GridView
            },
            tooltip = when (cameraManager.viewMode.value) {
                ViewMode.LIST -> stringResource(R.string.list)
                ViewMode.MAP -> stringResource(R.string.map)
                else -> stringResource(R.string.gallery)
            },
            visible = remainingActions-- > 0
        ) {
            showViewModeMenu = !showViewModeMenu
            onItemSelected()
        }
    }
    Box {
        var showSortMenu by remember { mutableStateOf(false) }
        SortModeMenu(showSortMenu) {
            showSortMenu = false
            onItemSelected()
        }
        MenuItem(
            icon = Icons.Rounded.Sort,
            tooltip = stringResource(R.string.sort),
            visible = cameraManager.viewMode.value != ViewMode.MAP && remainingActions-- > 0
        ) {
            showSortMenu = !showSortMenu
            onItemSelected()
        }
    }

    val overflowActions = ArrayList<Action>()
    for (action in actions) {
        if (!action.isMenu && action.condition) {
            if (remainingActions-- > 0) {
                MenuItem(
                    icon = action.icon,
                    tooltip = action.toolTip,
                    visible = true
                ) {
                    cameraManager.onSearchModeChanged(SearchMode.NAME)
                    onItemSelected()
                    action.onClick?.invoke()
                }
            }
            else {
                overflowActions.add(action)
            }
        }
    }

    if (remainingActions < 0) {
        Box {
            var showOverflowMenu by remember { mutableStateOf(false) }
            OverflowMenu(showOverflowMenu, overflowActions) {
                showOverflowMenu = false
                onItemSelected()
            }
            MenuItem(
                icon = Icons.Rounded.MoreVert,
                tooltip = stringResource(R.string.more),
                visible = true
            ) {
                showOverflowMenu = true
            }
        }
    }
}