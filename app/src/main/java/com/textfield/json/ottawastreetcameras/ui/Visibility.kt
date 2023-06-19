package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.runtime.Composable

@Composable
fun Visibility(visible: Boolean = true, child: @Composable () -> Unit) {
    if (visible) {
        child()
    }
}