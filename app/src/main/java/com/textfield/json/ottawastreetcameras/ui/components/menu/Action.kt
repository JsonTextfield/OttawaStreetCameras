package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class Action(
    val icon: ImageVector,
    val toolTip: String,
    val condition: Boolean,
    var checked: Boolean = false,
    val isMenu: Boolean = false,
    val onClick: (() -> Unit)? = null,
    val menuContent: (@Composable (expanded: Boolean) -> Unit)? = null,
)