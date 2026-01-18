package com.jsontextfield.core.ui.components.menu

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics

@Composable
fun OverflowMenuItem(
    icon: Int,
    tooltip: String = "",
    isVisible: Boolean = true,
    onClick: () -> Unit = {},
) {
    if (isVisible) {
        DropdownMenuItem(
            text = { Text(text = tooltip) },
            leadingIcon = { Icon(painterResource(icon), null) },
            onClick = onClick,
            modifier = Modifier.semantics(true){}
        )
    }
}