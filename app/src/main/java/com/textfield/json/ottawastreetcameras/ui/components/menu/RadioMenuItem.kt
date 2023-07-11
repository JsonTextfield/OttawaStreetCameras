package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RadioMenuItem(title: String, selected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        text = {
            Row {
                RadioButton(
                    selected = selected,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(0.dp),
                    onClick = onClick,
                )
                Text(
                    title,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 10.dp),
                )
            }
        },
        onClick = onClick,
    )
}