package com.textfield.json.ottawastreetcameras.ui.components.menu

import android.util.Log
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
fun ActionBar(actions: List<Action>, onItemSelected: () -> Unit) {
    val width = LocalConfiguration.current.screenWidthDp
    val maxActions = width / 48 / 3
    var remainingActions = maxActions
    Log.d("WIDTH", width.toString())
    Log.d("MAX_ACTIONS", remainingActions.toString())

    val overflowActions = ArrayList<Action>()
    for (action in actions) {
        if (action.condition) {
            if (remainingActions-- > 0) {
                if (action.isMenu) {
                    var showMenu by remember { mutableStateOf(false) }
                    IconMenu(showMenu, action) {
                        showMenu = !showMenu
                        onItemSelected()
                    }
                }
                else {
                    MenuItem(
                        icon = action.icon,
                        tooltip = action.toolTip,
                        visible = true
                    ) {
                        action.onClick()
                        onItemSelected()
                    }
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