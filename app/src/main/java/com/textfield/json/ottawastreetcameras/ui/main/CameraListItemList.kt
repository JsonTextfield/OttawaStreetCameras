package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.components.SectionIndex

@Composable
fun CameraListItemList(
    searchText: String,
    cameraState: CameraState,
    listState: LazyListState,
    onItemClick: (Camera) -> Unit = {},
    onItemLongClick: (Camera) -> Unit = {},
    onItemDismissed: (Camera) -> Unit = {},
    onFavouriteClick: (Camera) -> Unit = {},
) {
    Row(
        Modifier.padding(
            start = WindowInsets.safeDrawing.asPaddingValues()
                .calculateStartPadding(LayoutDirection.Ltr)
        ),
    ) {
        AnimatedVisibility(
            visible = cameraState.showSectionIndex,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally(),
        ) {
            SectionIndex(
                data = cameraState.getDisplayedCameras(searchText).map { it.sortableName },
                listState = listState,
            )
        }
        val cameras = cameraState.getDisplayedCameras(searchText)
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(LayoutDirection.Ltr)
            ),
            modifier = Modifier.semantics {
                collectionInfo = CollectionInfo(
                    rowCount = cameras.size,
                    columnCount = 1,
                )
            }
        ) {
            itemsIndexed(cameras, key = { index, camera -> camera.id }) { index, camera ->
                if (cameraState.filterMode == FilterMode.FAVOURITE) {
                    CameraListItem(
                        camera = camera,
                        showDistance = cameraState.sortMode == SortMode.DISTANCE && camera.distance > -1,
                        onClick = onItemClick,
                        onLongClick = onItemLongClick,
                        onFavouriteClick = onFavouriteClick,
                        modifier = Modifier
                            .semantics {
                                collectionItemInfo = CollectionItemInfo(
                                    rowIndex = index,
                                    columnIndex = 0,
                                    columnSpan = 1,
                                    rowSpan = 1,
                                )
                            }
                            .animateItem(),
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
                        enableDismissFromEndToStart = cameraState.selectedCameras.isEmpty(),
                        enableDismissFromStartToEnd = cameraState.selectedCameras.isEmpty(),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary)
                            .animateItem(),
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
                                onFavouriteClick = onFavouriteClick,
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

@Preview
@Composable
private fun CameraListItemListPreview() {
    val cameraList = List(10) {
        Camera(
            _name = BilingualObject(en = "Camera $it", fr = "Caméra $it"),
            _neighbourhood = BilingualObject(en = "Neighbourhood $it", fr = "Voisinage $it"),
        )
    }
    CameraListItemList("", CameraState(allCameras = cameraList), listState = LazyListState())
}