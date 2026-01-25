package com.jsontextfield.shared.ui.components.menu

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource

data class Action(
    val icon: DrawableResource,
    val tooltip: String,
    val isVisible: Boolean = true,
    val onClick: () -> Unit = {},
    val menuContent: (@Composable (expanded: Boolean) -> Unit)? = null,
)