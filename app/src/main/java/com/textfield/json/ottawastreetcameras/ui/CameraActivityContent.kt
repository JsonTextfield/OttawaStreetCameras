package com.textfield.json.ottawastreetcameras.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraActivityContent(cameras: List<Camera>, shuffle: Boolean = false) {
    val camera = cameras.random()

    LazyColumn {
        if (shuffle) {
            item {
                CameraView(camera, true)
            }
        } else {
            items(cameras.size) {
                CameraView(cameras[it], false)
            }
        }
    }
    val context = LocalContext.current
    PlainTooltipBox(tooltip = { Text("Back") }) {
        IconButton(modifier = Modifier
            .tooltipAnchor()
            .padding(5.dp)
            .background(
                color = BackButtonBackground,
                shape = RoundedCornerShape(10.dp)
            ), onClick = {
            (context as ComponentActivity).onBackPressedDispatcher.onBackPressed()
        }) {
            Icon(Icons.Rounded.ArrowBack, "Back")
        }
    }

}