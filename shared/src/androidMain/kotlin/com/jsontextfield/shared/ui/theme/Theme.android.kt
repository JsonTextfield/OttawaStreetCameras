package com.jsontextfield.shared.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import org.koin.core.context.GlobalContext

actual fun useDynamicColor(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

actual fun dynamicColorScheme(useDarkTheme: Boolean): ColorScheme? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = GlobalContext.get().get<Context>()
        return if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    return null
}

@Composable
actual fun setStatusBarColor(useDarkTheme: Boolean) {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
    }
}