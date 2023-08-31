package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortMode

@Composable
fun SortModeMenu(cameraViewModel: CameraViewModel, expanded: Boolean, onItemSelected: () -> Unit) {
    val cameraState by cameraViewModel.cameraState.collectAsState()
    val context = LocalContext.current
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected() }) {
        fun setSortMode(sortMode: SortMode) {
            cameraViewModel.changeSortMode(context, sortMode)
            onItemSelected()
        }
        RadioMenuItem(
            title = stringResource(R.string.sort_by_name),
            selected = cameraState.sortMode == SortMode.NAME,
            onClick = { setSortMode(SortMode.NAME) },
        )
        RadioMenuItem(
            title = stringResource(R.string.sort_by_distance),
            selected = cameraState.sortMode == SortMode.DISTANCE,
            onClick = { setSortMode(SortMode.DISTANCE) },
        )
        RadioMenuItem(
            title = stringResource(R.string.sort_by_neighbourhood),
            selected = cameraState.sortMode == SortMode.NEIGHBOURHOOD,
            onClick = { setSortMode(SortMode.NEIGHBOURHOOD) },
        )
    }
}