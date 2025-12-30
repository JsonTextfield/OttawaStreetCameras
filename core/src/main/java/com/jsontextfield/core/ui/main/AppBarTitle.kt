package com.jsontextfield.core.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jsontextfield.core.R
import com.jsontextfield.core.ui.SearchMode
import com.jsontextfield.core.ui.components.SearchBar
import com.jsontextfield.core.ui.components.SuggestionDropdown

@Composable
fun AppBarTitle(
    cameraState: CameraState = CameraState(),
    onTextChanged: (String) -> Unit = {},
    searchText: String = "",
    suggestions: List<String> = emptyList(),
    onNavigateToCitySelectionScreen: () -> Unit = {},
) {
    if (cameraState.selectedCameras.isNotEmpty()) {
        Text(
            pluralStringResource(
                R.plurals.selectedCameras,
                cameraState.selectedCameras.size,
                cameraState.selectedCameras.size
            ),
            modifier = Modifier.padding(10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    else {
        when (cameraState.searchMode) {
            SearchMode.NONE -> {
                FilledTonalButton(
                    modifier = Modifier
                        .widthIn(max = 200.dp)
                        .fillMaxWidth(11 / 12f),
                    onClick = onNavigateToCitySelectionScreen
                ) {
                    Text(stringResource(cameraState.currentCity.stringRes))
                }
            }

            SearchMode.NAME -> {
                SearchBar(
                    hintText = pluralStringResource(
                        R.plurals.search_hint,
                        cameraState.getDisplayedCameras().size,
                        cameraState.getDisplayedCameras().size
                    ),
                    value = searchText,
                    onValueChange = onTextChanged,
                )
            }

            SearchMode.NEIGHBOURHOOD -> {
                Box {
                    SuggestionDropdown(suggestions, onTextChanged)
                    SearchBar(
                        hintText = pluralStringResource(
                            R.plurals.search_hint_neighbourhood,
                            cameraState.neighbourhoods.size,
                            cameraState.neighbourhoods.size,
                        ),
                        value = searchText,
                        onValueChange = onTextChanged,
                    )
                }
            }
        }
    }
}