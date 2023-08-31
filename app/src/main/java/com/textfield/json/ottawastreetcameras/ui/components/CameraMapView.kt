package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.textfield.json.ottawastreetcameras.CameraViewModel
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CameraMapView(
    cameraViewModel: CameraViewModel,
    cameras: List<Camera>,
    isMyLocationEnabled: Boolean,
    onItemClick: (Camera) -> Unit,
    onItemLongClick: (Camera) -> Unit,
) {
    val cameraState by cameraViewModel.cameraState.collectAsState()
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.builder().target(LatLng(45.45, -75.69)).zoom(9f).build()
    }
    var bounds: LatLngBounds? = null
    if (cameras.isNotEmpty()) {
        val builder = LatLngBounds.builder()
        for (camera in cameras) {
            builder.include(LatLng(camera.lat, camera.lon))
        }
        bounds = builder.build()
    }
    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            latLngBoundsForCameraTarget = bounds,
            mapStyleOptions = if (isSystemInDarkTheme()) {
                MapStyleOptions.loadRawResourceStyle(context, R.raw.dark_mode)
            }
            else {
                null
            },
            isMyLocationEnabled = isMyLocationEnabled,
        ),
        onMapLoaded = {
            if (bounds != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 50))
                }
            }
        },
    ) {
        cameras.map { camera ->
            Marker(
                state = MarkerState(position = LatLng(camera.lat, camera.lon)),
                title = camera.name,
                onInfoWindowClick = { onItemClick(camera) },
                onInfoWindowLongClick = { onItemLongClick(camera) },
                icon = if (cameraState.selectedCameras.contains(camera)) {
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                }
                else if (camera.isFavourite) {
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                }
                else {
                    BitmapDescriptorFactory.defaultMarker()
                }
            )
        }
    }
}