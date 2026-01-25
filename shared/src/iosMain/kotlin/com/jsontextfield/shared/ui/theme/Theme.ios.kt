package com.jsontextfield.shared.ui.theme

actual fun useDynamicColor(): Boolean {
    return false
}

@Composable
actual fun dynamicColorScheme(useDarkTheme: Boolean): ColorScheme? {
    return null
}

@Composable
actual fun setStatusBarColor(useDarkTheme: Boolean) {
}