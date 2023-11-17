package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraListView(
    cameraViewModel: CameraViewModel,
    cameras: List<Camera>,
    modifier: Modifier,
    listState: LazyListState,
    onItemClick: (Camera) -> Unit,
    onItemLongClick: (Camera) -> Unit,
    onItemDismissed: (Camera) -> Unit,
) {
    val cameraState by cameraViewModel.cameraState.collectAsState()
    LazyColumn(modifier = modifier, state = listState) {
        items(cameras, { camera -> camera.hashCode() }) { camera ->
            if (cameraState.filterMode == FilterMode.FAVOURITE) {
                CameraListTile(cameraViewModel, camera, onItemClick, onItemLongClick)
            }
            else {
                val dismissState = remember {
                    DismissState(
                        DismissValue.Default,
                        positionalThreshold = { this.density * 200f },
                    )
                }
                if (dismissState.isDismissed(DismissDirection.EndToStart) || dismissState.isDismissed(DismissDirection.StartToEnd)) {
                    onItemDismissed(camera)
                }
                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier.background(colorResource(id = R.color.colorAccent)),
                    background = {
                        Icon(
                            Icons.Rounded.VisibilityOff,
                            "",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(0.1f)
                                .padding(10.dp),
                            tint = Color.White,
                        )
                        Text(
                            stringResource(if (camera.isVisible) R.string.hide else R.string.unhide),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(0.5f)
                        )
                        Icon(
                            Icons.Rounded.VisibilityOff,
                            "",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(0.1f)
                                .padding(10.dp),
                            tint = Color.White,
                        )
                    },
                    dismissContent = {
                        CameraListTile(cameraViewModel, camera, onItemClick, onItemLongClick)
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