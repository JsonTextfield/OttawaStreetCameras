package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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