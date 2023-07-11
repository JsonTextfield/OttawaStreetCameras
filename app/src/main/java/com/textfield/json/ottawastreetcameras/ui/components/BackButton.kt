package com.textfield.json.ottawastreetcameras.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackButton() {
    val context = LocalContext.current
    PlainTooltipBox(tooltip = { Text(stringResource(R.string.back)) }) {
        IconButton(modifier = Modifier
            .tooltipAnchor()
            .padding(5.dp)
            .background(
                color = colorResource(R.color.backButtonBackground),
                shape = RoundedCornerShape(10.dp)
            ), onClick = {
            (context as ComponentActivity).onBackPressedDispatcher.onBackPressed()
        }) {
            Icon(Icons.Rounded.ArrowBack, stringResource(id = R.string.back), tint = Color.Black)
        }
    }
}