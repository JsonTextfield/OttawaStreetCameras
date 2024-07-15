package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.components.SectionIndex
import com.textfield.json.ottawastreetcameras.ui.viewmodels.FilterMode
import com.textfield.json.ottawastreetcameras.ui.viewmodels.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.viewmodels.SortMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraListItemList(
    mainViewModel: MainViewModel,
    listState: LazyListState,
    onItemClick: (Camera) -> Unit = {},
    onItemLongClick: (Camera) -> Unit = {},
    onItemDismissed: (Camera) -> Unit = {},
) {
    val cameraState by mainViewModel.cameraState.collectAsState()
    Row {
        AnimatedVisibility(visible = cameraState.showSectionIndex) {
            SectionIndex(
                data = cameraState.displayedCameras.map { it.sortableName },
                listState = listState,
            )
        }
        val cameras = cameraState.displayedCameras
        LazyColumn(state = listState) {
            items(cameras, { camera -> camera.hashCode() }) { camera ->
                if (cameraState.filterMode == FilterMode.FAVOURITE) {
                    CameraListItem(
                        camera = camera,
                        showDistance = cameraState.sortMode == SortMode.DISTANCE && camera.distance > -1,
                        onClick = onItemClick,
                        onLongClick = onItemLongClick,
                        onFavouriteClick = { mainViewModel.favouriteCameras(listOf(it)) }
                    )
                }
                else {
                    val density = LocalDensity.current
                    val dismissState = remember {
                        SwipeToDismissBoxState(
                            density = density,
                            initialValue = SwipeToDismissBoxValue.Settled,
                            positionalThreshold = { density.density * 200f },
                        )
                    }
                    if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd
                        || dismissState.currentValue == SwipeToDismissBoxValue.EndToStart
                    ) {
                        onItemDismissed(camera)
                    }
                    SwipeToDismissBox(
                        state = dismissState,
                        modifier = Modifier.background(MaterialTheme.colorScheme.tertiary),
                        backgroundContent = {
                            ListItem(
                                modifier = Modifier.fillMaxSize(),
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                ),
                                leadingContent = {
                                    Icon(Icons.Rounded.VisibilityOff, "")
                                },
                                headlineContent = {
                                    Text(
                                        stringResource(if (camera.isVisible) R.string.hide else R.string.unhide),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                trailingContent = {
                                    Icon(Icons.Rounded.VisibilityOff, "")
                                },
                            )
                        },
                        content = {
                            CameraListItem(
                                camera = camera,
                                showDistance = cameraState.sortMode == SortMode.DISTANCE,
                                onClick = onItemClick,
                                onLongClick = onItemLongClick,
                            )
                        },
                    )
                }
            }
            item {
                Text(
                    pluralStringResource(R.plurals.camera_count, cameras.size, cameras.size),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}