package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortMode

@Composable
fun SortModeMenu(expanded: Boolean, currentSortMode: SortMode, onItemSelected: (sortMode: SortMode) -> Unit) {
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected(currentSortMode) }) {
        RadioMenuItem(
            title = stringResource(R.string.sort_by_name),
            selected = currentSortMode == SortMode.NAME,
            onClick = { onItemSelected(SortMode.NAME) },
        )
        RadioMenuItem(
            title = stringResource(R.string.sort_by_distance),
            selected = currentSortMode == SortMode.DISTANCE,
            onClick = { onItemSelected(SortMode.DISTANCE) },
        )
        RadioMenuItem(
            title = stringResource(R.string.sort_by_neighbourhood),
            selected = currentSortMode == SortMode.NEIGHBOURHOOD,
            onClick = { onItemSelected(SortMode.NEIGHBOURHOOD) },
        )
    }
}