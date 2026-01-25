package com.jsontextfield.shared.ui.main

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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.shared.entities.BilingualObject
import com.jsontextfield.shared.entities.Camera
import com.jsontextfield.shared.ui.FilterMode
import com.jsontextfield.shared.ui.SortMode
import com.jsontextfield.shared.ui.components.SectionIndex
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import streetcams.shared.generated.resources.Res
import streetcams.shared.generated.resources.camera_count
import streetcams.shared.generated.resources.hide
import streetcams.shared.generated.resources.round_visibility_off_24
import streetcams.shared.generated.resources.unhide
import kotlin.math.ceil
import kotlin.math.min

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
    val density = LocalDensity.current
    val widthDp =
        (LocalWindowInfo.current.containerSize.width / density.density - WindowInsets.safeDrawing.asPaddingValues()
            .calculateLeftPadding(
                LayoutDirection.Ltr
            ).value - WindowInsets.safeDrawing.asPaddingValues()
            .calculateRightPadding(LayoutDirection.Ltr).value).toInt()
    val columns = min((widthDp / 300).coerceIn(1, 4), ceil(3 / density.fontScale).toInt())
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
                gridState = gridState,
            )
        }
        val cameras = cameraState.getDisplayedCameras(searchText)
        LazyVerticalGrid(
            state = gridState,
            contentPadding = PaddingValues(
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 100.dp,
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(LayoutDirection.Ltr)
            ),
            columns = GridCells.Fixed(columns),
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
                                    Icon(painterResource(Res.drawable.round_visibility_off_24), "")
                                },
                                headlineContent = {
                                    Text(
                                        stringResource(if (camera.isVisible) Res.string.hide else Res.string.unhide),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                trailingContent = {
                                    Icon(painterResource(Res.drawable.round_visibility_off_24), "")
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
            item(span = { GridItemSpan(columns) }) {
                Text(
                    pluralStringResource(Res.plurals.camera_count, cameras.size, cameras.size),
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