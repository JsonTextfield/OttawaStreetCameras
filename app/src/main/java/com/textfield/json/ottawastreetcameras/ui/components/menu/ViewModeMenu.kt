package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun ViewModeMenu(
    expanded: Boolean,
    currentValue: ViewMode,
    onItemSelected: (value: ViewMode) -> Unit,
) {
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected(currentValue) }) {
        ViewMode.values().forEach {
            RadioMenuItem(
                title = stringResource(it.key),
                selected = currentValue == it,
                onClick = { onItemSelected(it) },
            )
        }
    }
}