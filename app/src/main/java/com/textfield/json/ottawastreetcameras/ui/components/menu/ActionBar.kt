package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.R

@Composable
fun ActionBar(actions: List<Action>, onItemSelected: () -> Unit = {}) {
    val maxActions = LocalConfiguration.current.screenWidthDp / 144

    val visibleActions = actions.filter { it.condition }

    val displayActions = visibleActions.take(maxActions)
    displayActions.forEach { action ->
        var showMenu by remember { mutableStateOf(false) }
        if (action.isMenu) {
            PopupMenu(
                showMenu = showMenu,
                menuContent = action.menuContent,
            )
        }
        MenuItem(
            icon = action.icon,
            tooltip = action.toolTip,
            onClick = {
                if (action.isMenu) {
                    showMenu = !showMenu
                }
                else {
                    action.onClick()
                }
                onItemSelected()
            }
        )
    }

    val overflowActions = visibleActions.drop(maxActions)
    if (overflowActions.isNotEmpty()) {
        Box {
            var showOverflowMenu by remember { mutableStateOf(false) }
            OverflowMenu(showOverflowMenu, overflowActions) {
                showOverflowMenu = false
                onItemSelected()
            }
            MenuItem(
                icon = Icons.Rounded.MoreVert,
                tooltip = stringResource(R.string.more),
                onClick = {
                    showOverflowMenu = true
                }
            )
        }
    }
}