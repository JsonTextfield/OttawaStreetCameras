package com.textfield.json.ottawastreetcameras.ui.components

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R


@Composable
fun ActionModeMenu(actions: List<Action>, onItemSelected: () -> Unit) {
    val context = LocalContext.current
    val cameraManager = CameraManager.getInstance()
    val selectedCameras = cameraManager.getSelectedCameras()
    val maxCameras = 8
    val width = LocalConfiguration.current.screenWidthDp
    val maxActions = width / 48 / 3
    var remainingActions = maxActions
    val overflowActions = ArrayList<Action>()
    Log.e("WIDTH", width.toString())
    Log.e("MAX_ACTIONS", remainingActions.toString())

    for (action in actions) {
        if (remainingActions-- > 0) {
            MenuItem(
                icon = action.icon,
                tooltip = action.toolTip,
                visible = action.condition
            ) {
                cameraManager.clearSelectedCameras()
                onItemSelected()
            }
        }
        else {
            overflowActions.add(action)
        }
    }
    /*
        MenuItem(icon = Icons.Rounded.Deselect, tooltip = stringResource(R.string.deselect_all), visible = true) {
            cameraManager.clearSelectedCameras()
            onItemSelected(R.string.deselect_all)
        }

        MenuItem(
            icon = Icons.Rounded.SelectAll,
            tooltip = stringResource(R.string.select_all),
            visible = selectedCameras.size < cameraManager.displayedCameras.size
        ) {
            //cameraManager.selectAllCameras()
            onItemSelected(R.string.select_all)
        }
        MenuItem(
            icon = Icons.Rounded.CameraAlt,
            tooltip = stringResource(R.string.show),
            visible = selectedCameras.size <= maxCameras
        ) {
            //showSelectedCameras()
            onItemSelected(R.string.show)
        }
        MenuItem(
            icon = Icons.Rounded.VisibilityOff,
            tooltip = stringResource(R.string.hide),
            visible = selectedCameras.any { it.isVisible }) {
            selectedCameras.forEach {
                it.isVisible = false
                cameraManager.hideCamera(context, it)
            }
            onItemSelected(R.string.hide)
        }
        MenuItem(
            icon = Icons.Rounded.Visibility,
            tooltip = stringResource(R.string.unhide),
            visible = selectedCameras.all { !it.isVisible }) {
            selectedCameras.forEach {
                it.isVisible = true
                cameraManager.hideCamera(context, it)
            }
            onItemSelected(R.string.unhide)
        }
        MenuItem(
            icon = Icons.Rounded.Star,
            tooltip = stringResource(R.string.add_to_favourites),
            visible = selectedCameras.any { !it.isFavourite }) {
            selectedCameras.forEach {
                it.isFavourite = true
                cameraManager.favouriteCamera(context, it)
            }
            onItemSelected(R.string.add_to_favourites)
        }
        MenuItem(
            icon = Icons.Rounded.StarBorder,
            tooltip = stringResource(R.string.remove_from_favourites),
            visible = selectedCameras.all { it.isFavourite }
        ) {
            selectedCameras.forEach {
                it.isFavourite = false
                cameraManager.favouriteCamera(context, it)
            }
            onItemSelected(R.string.remove_from_favourites)
        }
     */
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
