package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ui.components.SearchBar
import com.textfield.json.ottawastreetcameras.ui.components.SuggestionDropdown

@Composable
fun AppBarTitle(
    cameraState: CameraState = CameraState(),
    onTextChanged: (String) -> Unit = {},
    searchText: String = "",
    suggestions: List<String> = emptyList(),
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
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    else {
        when (cameraState.searchMode) {
            SearchMode.NONE -> {
                val title = stringResource(id = cameraState.filterMode.key)
                Text(
                    title,
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )
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