package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RadioMenuItem(
    title: String = "",
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    DropdownMenuItem(
        contentPadding = PaddingValues(0.dp),
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isSelected,
                    modifier = Modifier.padding(0.dp),
                    onClick = onClick,
                )
                Text(
                    title,
                    modifier = Modifier.padding(end = 10.dp),
                )
            }
        },
        onClick = onClick,
    )
}