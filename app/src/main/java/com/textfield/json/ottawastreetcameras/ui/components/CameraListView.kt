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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraListView(
    cameras: List<Camera>,
    modifier: Modifier,
    listState: LazyListState,
    onItemClick: (Camera) -> Unit,
    onItemLongClick: (Camera) -> Unit,
) {
    LazyColumn(modifier = modifier, state = listState) {
        items(cameras, { camera -> camera.hashCode() }) { camera ->
            val cameraManager = CameraManager.getInstance()
            val dismissState = remember { DismissState(DismissValue.Default) }
            val context = LocalContext.current
            if (dismissState.isDismissed(DismissDirection.EndToStart) || dismissState.isDismissed(DismissDirection.StartToEnd)) {
                camera.isVisible = !camera.isVisible
                cameraManager.hideCamera(context, camera)
            }
            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier.background(colorResource(id = R.color.colorAccent)),
                background = {
                    Icon(
                        Icons.Rounded.VisibilityOff, "", modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(0.1f)
                            .padding(10.dp)
                    )
                    Text(
                        stringResource(if (camera.isVisible) R.string.hide else R.string.unhide),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(0.5f)
                    )
                    Icon(
                        Icons.Rounded.VisibilityOff, "", modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(0.1f)
                            .padding(10.dp)
                    )
                },
                dismissContent = {
                    CameraListTile(camera, onItemClick, onItemLongClick)
                })
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