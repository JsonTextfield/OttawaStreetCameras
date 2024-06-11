package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchBarContent(
    hintText: String = "",
    value: String = "",
    onValueChange: (String) -> Unit = {},
) {
    val textFieldValue = TextFieldValue(value, TextRange(value.length))
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { onValueChange(it.text) },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.inversePrimary),
        decorationBox = { innerTextField ->
            Box {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(.75f)
                    ) {
                        innerTextField()
                    }

                    if (value.isNotEmpty()) {
                        IconButton(
                            onClick = { onValueChange("") },
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(.25f)
                        ) {
                            Icon(
                                Icons.Rounded.Clear,
                                stringResource(id = R.string.clear),
                                tint = MaterialTheme.colorScheme.onPrimary,
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
                        fontSize = 14.sp,
                        softWrap = false,
                        color = MaterialTheme.colorScheme.inversePrimary,
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