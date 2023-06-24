package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SortMode


@Composable
fun SortModeMenu(expanded: Boolean, onItemSelected: () -> Unit) {
    val context = LocalContext.current
    val cameraManager = CameraManager.getInstance()
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected() }) {
        fun setSortMode(sortMode: SortMode) {
            cameraManager.sortMode = sortMode
            onItemSelected()
        }

        DropdownMenuItem(
            text = { Text(context.getString(R.string.sort_by_name)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.sortMode == SortMode.NAME,
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
            text = { Text(context.getString(R.string.sort_by_distance)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.sortMode == SortMode.DISTANCE,
                    onClick = { setSortMode(SortMode.DISTANCE) },
                )
            },
            onClick = {
                setSortMode(SortMode.DISTANCE)
            },
        )
        DropdownMenuItem(
            text = { Text(context.getString(R.string.sort_by_neighbourhood)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.sortMode == SortMode.NEIGHBOURHOOD,
                    onClick = { setSortMode(SortMode.NEIGHBOURHOOD) },
                )
            },
            onClick = {
                setSortMode(SortMode.NEIGHBOURHOOD)
            },
        )
    }
}