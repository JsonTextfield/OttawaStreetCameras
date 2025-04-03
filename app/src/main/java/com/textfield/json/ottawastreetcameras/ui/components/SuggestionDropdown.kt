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
    suggestions: List<String>,
    onItemSelected: (item: String) -> Unit,
) {
    DropdownMenu(
        expanded = suggestions.isNotEmpty(),
        modifier = Modifier
            .width(200.dp)
            .heightIn(max = 200.dp),
        onDismissRequest = { },
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
        suggestions = "hello world this is a test".split(" "),
        onItemSelected = {},
    )
}