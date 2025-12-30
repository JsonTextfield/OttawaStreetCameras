package com.jsontextfield.streetcamstv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.jsontextfield.core.ui.ThemeMode
import com.jsontextfield.core.ui.theme.LocalTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun OttawaStreetCamsTheme(
    theme: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    LocalTheme = compositionLocalOf { theme }
    val darkTheme = when (theme) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        darkTheme -> darkScheme
        else -> lightScheme
    }
    CompositionLocalProvider(LocalTheme provides theme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}