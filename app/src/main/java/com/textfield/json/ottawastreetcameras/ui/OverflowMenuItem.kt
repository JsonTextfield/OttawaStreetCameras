package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun OverflowMenuItem(icon: ImageVector, tooltip: String, visible: Boolean, onClick: () -> Unit) {
    Visibility(
        visible = visible,
        child = {
            DropdownMenuItem(
                text = { Text(text = tooltip) },
                leadingIcon = { Icon(icon, tooltip) },
                onClick = onClick,
            )
        }
    )
}