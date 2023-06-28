package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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