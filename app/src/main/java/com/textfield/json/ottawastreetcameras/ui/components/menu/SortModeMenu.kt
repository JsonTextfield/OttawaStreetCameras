package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortMode

@Composable
fun SortModeMenu(expanded: Boolean, onItemSelected: () -> Unit) {
    val cameraManager = CameraManager.getInstance()
    val context = LocalContext.current
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected() }) {
        fun setSortMode(sortMode: SortMode) {
            cameraManager.changeSortMode(context, sortMode)
            onItemSelected()
        }
        RadioMenuItem(
            title = stringResource(R.string.sort_by_name),
            selected = cameraManager.cameraState.value?.sortMode == SortMode.NAME,
            onClick = { setSortMode(SortMode.NAME) },
        )
        RadioMenuItem(
            title = stringResource(R.string.sort_by_distance),
            selected = cameraManager.cameraState.value?.sortMode == SortMode.DISTANCE,
            onClick = { setSortMode(SortMode.DISTANCE) },
        )
        RadioMenuItem(
            title = stringResource(R.string.sort_by_neighbourhood),
            selected = cameraManager.cameraState.value?.sortMode == SortMode.NEIGHBOURHOOD,
            onClick = { setSortMode(SortMode.NEIGHBOURHOOD) },
        )
    }
}