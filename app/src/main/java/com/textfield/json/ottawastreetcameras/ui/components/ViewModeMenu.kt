package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun ViewModeMenu(expanded: Boolean, onItemSelected: () -> Unit) {
    val cameraManager = CameraManager.getInstance()
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected() }) {
        fun setViewMode(viewMode: ViewMode) {
            cameraManager.changeViewMode(viewMode)
            onItemSelected()
        }
        DropdownMenuItem(
            text = { Text(stringResource(R.string.list)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.cameraState.value?.viewMode == ViewMode.LIST,
                    onClick = {
                        setViewMode(ViewMode.LIST)
                    },
                )
            },
            onClick = {
                setViewMode(ViewMode.LIST)
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.map)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.cameraState.value?.viewMode == ViewMode.MAP,
                    onClick = {
                        setViewMode(ViewMode.MAP)
                    },
                )
            },
            onClick = {
                setViewMode(ViewMode.MAP)
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.gallery)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.cameraState.value?.viewMode == ViewMode.GALLERY,
                    onClick = {
                        setViewMode(ViewMode.GALLERY)
                    },
                )
            },
            onClick = {
                setViewMode(ViewMode.GALLERY)
            },
        )
    }
}