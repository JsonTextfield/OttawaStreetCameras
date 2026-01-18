package com.jsontextfield.core.ui.components.menu

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable

data class Action(
    @DrawableRes val icon: Int,
    val tooltip: String,
    val isVisible: Boolean = true,
    val onClick: () -> Unit = {},
    val menuContent: (@Composable (expanded: Boolean) -> Unit)? = null,
)