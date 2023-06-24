package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun ViewModeMenu(expanded: Boolean, onItemSelected: () -> Unit) {
    val context = LocalContext.current
    val cameraManager = CameraManager.getInstance()
    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected() }) {
        fun setViewMode(viewMode: ViewMode) {
            cameraManager.viewMode = viewMode
            onItemSelected()
        }
        DropdownMenuItem(
            text = { Text(context.getString(R.string.list)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.viewMode == ViewMode.LIST,
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
            text = { Text(context.getString(R.string.map)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.viewMode == ViewMode.MAP,
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
            text = { Text(context.getString(R.string.gallery)) },
            leadingIcon = {
                RadioButton(
                    selected = cameraManager.viewMode == ViewMode.GALLERY,
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