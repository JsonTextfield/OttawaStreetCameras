package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun ViewModeMenu(expanded: Boolean, currentViewMode: ViewMode, onItemSelected: (viewMode: ViewMode) -> Unit) {
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected(currentViewMode) }) {
        RadioMenuItem(
            title = stringResource(R.string.list),
            selected = currentViewMode == ViewMode.LIST,
            onClick = { onItemSelected(ViewMode.LIST) },
        )
        RadioMenuItem(
            title = stringResource(R.string.map),
            selected = currentViewMode == ViewMode.MAP,
            onClick = { onItemSelected(ViewMode.MAP) },
        )
        RadioMenuItem(
            title = stringResource(R.string.gallery),
            selected = currentViewMode == ViewMode.GALLERY,
            onClick = { onItemSelected(ViewMode.GALLERY) },
        )
    }
}