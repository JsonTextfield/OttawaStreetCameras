package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AppBarTitle(listState: LazyListState) {
    val cameraManager = CameraManager.getInstance()
    val cameraState = cameraManager.cameraState.observeAsState()
    if (cameraState.value?.selectedCameras?.isNotEmpty() == true) {
        Text(
            pluralStringResource(
                R.plurals.selectedCameras,
                cameraState.value?.selectedCameras?.size ?: 0,
                cameraState.value?.selectedCameras?.size ?: 0
            ), color = Color.White, modifier = Modifier
                .padding(10.dp)
                .clickable {
                    CoroutineScope(Dispatchers.Main).launch {
                        listState.scrollToItem(0)
                    }
                })
    }
    else {
        when (cameraState.value?.searchMode) {
            SearchMode.NONE -> {
                val title = when (cameraState.value?.filterMode) {
                    FilterMode.FAVOURITE -> stringResource(id = R.string.favourites)
                    FilterMode.HIDDEN -> stringResource(id = R.string.hidden_cameras)
                    FilterMode.VISIBLE -> stringResource(id = R.string.app_name)
                    else -> stringResource(R.string.app_name)
                }
                Text(title, color = Color.White, modifier = Modifier
                    .clickable {
                        CoroutineScope(Dispatchers.Main).launch {
                            listState.scrollToItem(0, 0)
                        }
                    }
                    .padding(10.dp)
                )
            }

            SearchMode.NAME -> {
                SearchBar(
                    pluralStringResource(
                        R.plurals.search_hint,
                        cameraState.value?.displayedCameras?.size ?: 0,
                        cameraState.value?.displayedCameras?.size ?: 0
                    )
                ) {
                    cameraManager.searchCameras(it)
                }
            }

            SearchMode.NEIGHBOURHOOD -> {
                Box {
                    var value by rememberSaveable { mutableStateOf("") }
                    val suggestionList = cameraState.value?.neighbourhoods?.filter {
                        it.name.contains(value, true)
                    }?.map {
                        it.name
                    }
                    val expanded = cameraState.value?.searchMode == SearchMode.NEIGHBOURHOOD
                                   && value.isNotEmpty()
                                   && suggestionList?.isNotEmpty() == true
                                   && suggestionList.all { !it.equals(value, true) }

                    SuggestionDropdown(expanded, suggestionList ?: ArrayList(), value) {
                        value = it
                        cameraManager.searchCameras(value)
                    }
                    SearchBarContent(
                        pluralStringResource(
                            R.plurals.search_hint_neighbourhood,
                            cameraState.value?.neighbourhoods?.size ?: 0,
                            cameraState.value?.neighbourhoods?.size ?: 0,
                        ), value
                    ) {
                        value = it
                        cameraManager.searchCameras(value)
                    }
                }
            }

            else -> {}
        }
    }
}