package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun AppBarTitle(cameraViewModel: CameraViewModel, onClick: () -> Unit = {}) {
    val cameraState by cameraViewModel.cameraState.collectAsState()
    if (cameraState.selectedCameras.isNotEmpty()) {
        Text(
            pluralStringResource(
                R.plurals.selectedCameras,
                cameraState.selectedCameras.size,
                cameraState.selectedCameras.size
            ),
            color = Color.White,
            modifier = Modifier
                .clickable(enabled = cameraState.viewMode != ViewMode.MAP, onClick = onClick)
                .padding(10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    else {
        when (cameraState.searchMode) {
            SearchMode.NONE -> {
                val title = when (cameraState.filterMode) {
                    FilterMode.FAVOURITE -> stringResource(id = R.string.favourites)
                    FilterMode.HIDDEN -> stringResource(id = R.string.hidden_cameras)
                    FilterMode.VISIBLE -> stringResource(id = R.string.app_name)
                }
                Text(
                    title, color = Color.White,
                    modifier = Modifier
                        .clickable(enabled = cameraState.viewMode != ViewMode.MAP, onClick = onClick)
                        .padding(10.dp),
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            SearchMode.NAME -> {
                SearchBar(
                    pluralStringResource(
                        R.plurals.search_hint,
                        cameraState.displayedCameras.size,
                        cameraState.displayedCameras.size
                    )
                ) {
                    cameraViewModel.searchCameras(cameraState.searchMode, it)
                }
            }

            SearchMode.NEIGHBOURHOOD -> {
                Box {
                    var value by rememberSaveable { mutableStateOf("") }
                    val suggestionList = cameraState.neighbourhoods.filter {
                        it.contains(value, true)
                    }
                    val expanded =
                        cameraState.searchMode == SearchMode.NEIGHBOURHOOD
                        && value.isNotEmpty()
                        && suggestionList.isNotEmpty()
                        && suggestionList.all {
                            !it.equals(value, true)
                        }

                    SuggestionDropdown(expanded, suggestionList, value) {
                        value = it
                        cameraViewModel.searchCameras(cameraState.searchMode, value)
                    }
                    SearchBarContent(
                        pluralStringResource(
                            R.plurals.search_hint_neighbourhood,
                            cameraState.neighbourhoods.size,
                            cameraState.neighbourhoods.size,
                        ), value
                    ) {
                        value = it
                        cameraViewModel.searchCameras(cameraState.searchMode, value)
                    }
                }
            }
        }
    }
}