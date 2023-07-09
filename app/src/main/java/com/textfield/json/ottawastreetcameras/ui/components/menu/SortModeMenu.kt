package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortMode


@Composable
fun SortModeMenu(expanded: Boolean, onItemSelected: () -> Unit) {
    val cameraManager = CameraManager.getInstance()
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected() }) {
        fun setSortMode(sortMode: SortMode) {
            cameraManager.changeSortMode(sortMode)
            onItemSelected()
        }

        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_by_name)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.cameraState.value?.sortMode == SortMode.NAME,
                    onClick = {
                        setSortMode(SortMode.NAME)
                    },
                )
            },
            onClick = {
                setSortMode(SortMode.NAME)
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_by_distance)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.cameraState.value?.sortMode == SortMode.DISTANCE,
                    onClick = { setSortMode(SortMode.DISTANCE) },
                )
            },
            onClick = {
                setSortMode(SortMode.DISTANCE)
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_by_neighbourhood)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.cameraState.value?.sortMode == SortMode.NEIGHBOURHOOD,
                    onClick = { setSortMode(SortMode.NEIGHBOURHOOD) },
                )
            },
            onClick = {
                setSortMode(SortMode.NEIGHBOURHOOD)
            },
        )
    }
}