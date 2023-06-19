package com.textfield.json.ottawastreetcameras.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.entities.Camera


@Composable
fun CameraActivityView(cameras: List<Camera>) {
    LazyColumn {
        items(cameras.size) {
            CameraView(cameras[it])
        }
    }
    val context = LocalContext.current
    IconButton(modifier = Modifier
        .padding(5.dp)
        .background(
            color = Color(0x66FFFFFF),
            shape = RoundedCornerShape(10.dp)
        ), onClick = {
        (context as ComponentActivity).onBackPressedDispatcher.onBackPressed()
    }) {
        Icon(Icons.Rounded.ArrowBack, "Back")
    }
}