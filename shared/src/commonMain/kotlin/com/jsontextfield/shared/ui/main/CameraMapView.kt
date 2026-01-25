package com.jsontextfield.shared.ui.main
import androidx.compose.runtime.Composable
import com.jsontextfield.shared.entities.Camera

@Composable
expect fun CameraMapView(
    searchText: String,
    cameraState: CameraState,
    isMyLocationEnabled: Boolean,
    onItemClick: (Camera) -> Unit,
    onItemLongClick: (Camera) -> Unit,
)