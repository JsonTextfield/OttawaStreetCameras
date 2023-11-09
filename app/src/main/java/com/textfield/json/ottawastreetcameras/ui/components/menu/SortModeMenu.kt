package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.SortMode

@Composable
fun SortModeMenu(expanded: Boolean, currentSortMode: SortMode, onItemSelected: (sortMode: SortMode) -> Unit) {
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected(currentSortMode) }) {
        SortMode.values().forEach {
            RadioMenuItem(
                title = stringResource(it.key),
                selected = currentSortMode == it,
                onClick = { onItemSelected(it) },
            )
        }
    }
}