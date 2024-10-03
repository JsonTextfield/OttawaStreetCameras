package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun SuggestionDropdown(
    expanded: Boolean,
    suggestions: List<String>,
    text: String,
    onItemSelected: (item: String) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        modifier = Modifier
            .width(200.dp)
            .heightIn(0.dp, 200.dp),
        onDismissRequest = { onItemSelected(text) },
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
    ) {
        suggestions.forEach {
            DropdownMenuItem(
                text = { Text(it) },
                onClick = { onItemSelected(it) }
            )
        }
    }
}

@Preview(widthDp = 300, showSystemUi = true, showBackground = true)
@Composable
private fun SuggestionDropdownPreview() {
    SuggestionDropdown(
        expanded = true,
        suggestions = "hello world this is a test".split(" "),
        text = "",
        onItemSelected = {},
    )
}