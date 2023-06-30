package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (!useDarkTheme) {
        lightColorScheme(
            primary = colorResource(id = R.color.colorAccent),
            surfaceTint = colorResource(id = R.color.colorAccent),
            primaryContainer = Color.White,
            secondaryContainer = Color.White,
            tertiaryContainer = Color.White,
        )
    } else {
        darkColorScheme(
            primary = colorResource(id = R.color.colorAccent),
            background = Color.Black,
            surfaceTint = Color.Black,
            primaryContainer = Color.Black,
            secondaryContainer = Color.Black,
            tertiaryContainer = Color.Black,
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(10.dp))
    )
}