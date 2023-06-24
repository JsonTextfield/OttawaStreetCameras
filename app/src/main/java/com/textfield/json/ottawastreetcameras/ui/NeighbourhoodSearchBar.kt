package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun NeighbourhoodSearchBar(hintText: String, onValueChange: (String) -> Unit) {
    var value by rememberSaveable { mutableStateOf("") }
    NeighbourhoodSearchBarContent(hintText, value) {
        value = it
        onValueChange(value)
    }
}

@Composable
private fun NeighbourhoodSearchBarContent(hintText: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value,
        singleLine = true,
        placeholder = {
            Text(hintText, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        onValueChange = onValueChange,
        modifier = Modifier.heightIn(0.dp, 50.dp),
        textStyle = TextStyle(
            fontSize = 12.sp,
        )
    )
}