package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (!useDarkTheme) {
        lightColorScheme()
    } else {
        darkColorScheme(
            background = Color.Black,
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}