package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun ViewModeMenu(expanded: Boolean, onItemSelected: () -> Unit) {
    val cameraManager = CameraManager.getInstance()
    val context = LocalContext.current
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected() }) {
        fun setViewMode(viewMode: ViewMode) {
            cameraManager.changeViewMode(context, viewMode)
            onItemSelected()
        }
        RadioMenuItem(
            title = stringResource(R.string.list),
            selected = cameraManager.cameraState.value?.viewMode == ViewMode.LIST,
            onClick = { setViewMode(ViewMode.LIST) },
        )
        RadioMenuItem(
            title = stringResource(R.string.map),
            selected = cameraManager.cameraState.value?.viewMode == ViewMode.MAP,
            onClick = { setViewMode(ViewMode.MAP) },
        )
        RadioMenuItem(
            title = stringResource(R.string.gallery),
            selected = cameraManager.cameraState.value?.viewMode == ViewMode.GALLERY,
            onClick = { setViewMode(ViewMode.GALLERY) },
        )
    }
}