package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.SortMode

@Composable
fun SortModeMenu(
    expanded: Boolean,
    currentValue: SortMode,
    onItemSelected: (value: SortMode) -> Unit,
) {
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected(currentValue) }) {
        SortMode.values().forEach {
            RadioMenuItem(
                title = stringResource(it.key),
                selected = currentValue == it,
                onClick = { onItemSelected(it) },
            )
        }
    }
}