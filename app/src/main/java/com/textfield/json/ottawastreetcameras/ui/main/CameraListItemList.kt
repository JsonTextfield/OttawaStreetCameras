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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
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
    gridState: LazyGridState,
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
                listState = gridState,
            )
        }
        val cameras = cameraState.getDisplayedCameras(searchText)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(240.dp),
            state = gridState,
            contentPadding = PaddingValues(
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(LayoutDirection.Ltr)
            ),
        ) {
            items(cameras, key = { it.id }) { camera ->
                if (cameraState.filterMode == FilterMode.FAVOURITE) {
                    CameraListItem(
                        camera = camera,
                        showDistance = cameraState.sortMode == SortMode.DISTANCE && camera.distance > -1,
                        onClick = onItemClick,
                        onLongClick = onItemLongClick,
                        onFavouriteClick = onFavouriteClick,
                        modifier = Modifier.animateItem(),
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
                                    Icon(painterResource(R.drawable.round_visibility_off_24), "")
                                },
                                headlineContent = {
                                    Text(
                                        stringResource(if (camera.isVisible) R.string.hide else R.string.unhide),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                trailingContent = {
                                    Icon(painterResource(R.drawable.round_visibility_off_24), "")
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
    CameraListItemList("", CameraState(allCameras = cameraList), gridState = LazyGridState())
}