package com.jsontextfield.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import streetcams.shared.generated.resources.Res
import streetcams.shared.generated.resources.clear
import streetcams.shared.generated.resources.round_clear_24

@Composable
fun SearchBar(
    value: String = "",
    hintText: String = "",
    onValueChange: (String) -> Unit = {},
) {
    val textFieldValue = TextFieldValue(value, TextRange(value.length))
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { onValueChange(it.text) },
        modifier = Modifier
            .padding(vertical = 12.dp)
            .height(40.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(24.dp)
            )
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.inversePrimary),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(.7f)
                    ) {
                        innerTextField()
                    }

                    if (value.isNotEmpty()) {
                        IconButton(
                            onClick = { onValueChange("") },
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(.3f)
                        ) {
                            Icon(
                                painterResource(Res.drawable.round_clear_24),
                                stringResource(Res.string.clear),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
                if (value.isEmpty()) {
                    Text(
                        hintText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .basicMarquee()
                            .align(Alignment.CenterStart),
                        maxLines = 1,
                        softWrap = false,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    )
                }
            }
        }
    )

    val windowInfo = LocalWindowInfo.current
    LaunchedEffect(windowInfo) {
        snapshotFlow { windowInfo.isWindowFocused }.collect { isWindowFocused ->
            if (isWindowFocused) {
                focusRequester.requestFocus()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SearchBarPreview() {
    TopAppBar(title = { SearchBar(hintText = "Search") })
}
