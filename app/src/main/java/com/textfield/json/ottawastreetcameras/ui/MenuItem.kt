@file:OptIn(ExperimentalMaterial3Api::class)

package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun MenuItem(icon: ImageVector, tooltip: String, visible: Boolean, onClick: () -> Unit) {
    Visibility(
        visible = visible,
        child = {
            PlainTooltipBox(
                tooltip = { Text(text = tooltip, modifier = Modifier.padding(10.dp)) },
            ) {
                IconButton(
                    modifier = Modifier.tooltipAnchor(),
                    content = {
                        Icon(icon, tooltip)
                    },
                    onClick = onClick,
                )
            }
        }
    )
}