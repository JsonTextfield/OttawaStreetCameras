package com.jsontextfield.shared.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jsontextfield.shared.ui.SearchMode
import com.jsontextfield.shared.ui.components.SearchBar
import com.jsontextfield.shared.ui.components.SuggestionDropdown
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import streetcams.shared.generated.resources.Res
import streetcams.shared.generated.resources.search_hint
import streetcams.shared.generated.resources.search_hint_neighbourhood
import streetcams.shared.generated.resources.selectedCameras

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
                Res.plurals.selectedCameras,
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
                    Text(stringResource(cameraState.city.stringRes))
                }
            }

            SearchMode.NAME -> {
                SearchBar(
                    hintText = pluralStringResource(
                        Res.plurals.search_hint,
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
                            Res.plurals.search_hint_neighbourhood,
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