package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.runtime.Composable

@Composable
fun Visibility(visible: Boolean = true, content: @Composable () -> Unit) {
    if (visible) {
        content()
    }
}