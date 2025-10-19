package com.jsontextfield.core.ui.components.menu

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItem(
    @DrawableRes icon: Int,
    tooltip: String = "",
    isVisible: Boolean = true,
    onClick: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = isVisible,
        content = {
            TooltipBox(
                state = rememberTooltipState(),
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(text = tooltip, modifier = Modifier.padding(8.dp))
                    }
                },
            ) {
                IconButton(
                    content = { Icon(painterResource(icon), tooltip) },
                    onClick = onClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        }
    )
}