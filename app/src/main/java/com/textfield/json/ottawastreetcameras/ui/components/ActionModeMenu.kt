package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R


@Composable
fun ActionModeMenu(onItemSelected: (id: Int) -> Unit) {
    val context = LocalContext.current
    val cameraManager = CameraManager.getInstance()
    val selectedCameras = cameraManager.getSelectedCameras()
    val maxCameras = 8
    MenuItem(icon = Icons.Rounded.Clear, tooltip = stringResource(R.string.clear), visible = true) {
        cameraManager.clearSelectedCameras()
        onItemSelected(R.string.clear)
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
}
