package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlin.math.ceil


@Composable
fun CameraGalleryView(
    searchText: String,
    cameraState: CameraState,
    gridState: LazyGridState,
    onItemClick: (Camera) -> Unit,
    onItemLongClick: (Camera) -> Unit,
) {
    val cameras = cameraState.getDisplayedCameras(searchText)
    val columns =
        (LocalWindowInfo.current.containerSize.width / LocalDensity.current.density / 120).toInt()
            .coerceIn(2, 8)
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(
            top = 10.dp,
            start = WindowInsets.safeDrawing.asPaddingValues()
                .calculateStartPadding(LayoutDirection.Ltr) + 10.dp,
            end = WindowInsets.safeDrawing.asPaddingValues()
                .calculateEndPadding(LayoutDirection.Ltr) + 10.dp,
            bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 100.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.semantics {
            collectionInfo = CollectionInfo(
                rowCount = ceil(cameras.size / columns.toDouble()).toInt(),
                columnCount = columns,
            )
        }
    ) {
        itemsIndexed(cameras, key = { index, camera -> camera.id }) { index, camera ->
            CameraGalleryTile(
                camera = camera,
                onClick = onItemClick,
                onLongClick = onItemLongClick,
                modifier = Modifier
                    .semantics(mergeDescendants = true) {
                        collectionItemInfo = CollectionItemInfo(
                            rowIndex = index / columns,
                            columnIndex = index % columns,
                            columnSpan = 1,
                            rowSpan = 1,
                        )
                    }
                    .animateItem(),
            )
        }

        item {
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(10.dp))
                    .aspectRatio(1f)
            ) {
                Text(
                    pluralStringResource(
                        R.plurals.camera_count,
                        cameras.size,
                        cameras.size
                    ).replace(" ", "\n"),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@PreviewScreenSizes
@PreviewFontScale
@Composable
private fun CameraGalleryViewPreview() {
    val cameraList =
        List(10) { Camera(_name = BilingualObject(en = "Camera $it", fr = "Caméra $it")) }
    CameraGalleryView("", CameraState(allCameras = cameraList), LazyGridState(), {}, {})
}