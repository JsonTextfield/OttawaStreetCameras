package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun ViewModeMenu(cameraViewModel: CameraViewModel, expanded: Boolean, onItemSelected: () -> Unit) {
    val cameraState by cameraViewModel.cameraState.collectAsState()
    val context = LocalContext.current
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected() }) {
        fun setViewMode(viewMode: ViewMode) {
            cameraViewModel.changeViewMode(context, viewMode)
            onItemSelected()
        }
        RadioMenuItem(
            title = stringResource(R.string.list),
            selected = cameraState.viewMode == ViewMode.LIST,
            onClick = { setViewMode(ViewMode.LIST) },
        )
        RadioMenuItem(
            title = stringResource(R.string.map),
            selected = cameraState.viewMode == ViewMode.MAP,
            onClick = { setViewMode(ViewMode.MAP) },
        )
        RadioMenuItem(
            title = stringResource(R.string.gallery),
            selected = cameraState.viewMode == ViewMode.GALLERY,
            onClick = { setViewMode(ViewMode.GALLERY) },
        )
    }
}