package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.textfield.json.ottawastreetcameras.R

@Composable
fun SearchBar(hintText: String, onValueChange: (String) -> Unit) {
    var value by rememberSaveable { mutableStateOf("") }
    SearchBarContent(hintText, value) {
        value = it
        onValueChange(value)
    }
}

@Composable
private fun SearchBarContent(hintText: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value, singleLine = true,
        placeholder = {
            Text(
                hintText,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                modifier = Modifier.padding(0.dp).fillMaxSize()
            )
        },
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = Color.White
        ),
        shape = CircleShape,
        suffix = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Rounded.Clear, contentDescription = stringResource(id = R.string.clear))
                }
            }
        }, colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}