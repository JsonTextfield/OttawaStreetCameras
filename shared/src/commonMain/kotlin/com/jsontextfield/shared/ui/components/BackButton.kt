package com.jsontextfield.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jsontextfield.shared.ui.theme.backButtonBackground
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import streetcams.shared.generated.resources.Res
import streetcams.shared.generated.resources.back
import streetcams.shared.generated.resources.round_arrow_back_24

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackButton(onClick: () -> Unit = {}) {
    TooltipBox(
        state = rememberTooltipState(),
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(stringResource(Res.string.back), modifier = Modifier.padding(12.dp))
            }
        },
    ) {
        IconButton(
            modifier = Modifier
                .padding(5.dp)
                .background(
                    color = backButtonBackground,
                    shape = RoundedCornerShape(12.dp)
                ),
            onClick = onClick,
        ) {
            Icon(
                painterResource(Res.drawable.round_arrow_back_24),
                stringResource(Res.string.back),
                tint = Color.Black
            )
        }
    }
}

@Preview
@Composable
private fun BackButtonPreview() {
    BackButton()
}