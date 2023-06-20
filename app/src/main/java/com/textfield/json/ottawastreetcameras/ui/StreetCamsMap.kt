package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera


@Composable
fun StreetCamsMap(cameras: List<Camera>, onItemClick: (Camera) -> Unit) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(
                45.4, -75.7
            ), 9f
        )
    }
    var bounds: LatLngBounds? = null
    val builder = LatLngBounds.builder()
    for (camera in cameras) {
        builder.include(LatLng(camera.lat, camera.lon))
    }
    if (cameras.isNotEmpty()) {
        bounds = builder.build()
    }
    val context = LocalContext.current
    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            latLngBoundsForCameraTarget = bounds,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.dark_mode)
        ),
        onMapLoaded = {
            if (bounds != null) {
                cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 50))
            }
        },
    ) {
        cameras.map { camera ->
            Marker(
                state = MarkerState(position = LatLng(camera.lat, camera.lon)),
                title = camera.getName(),
                onInfoWindowClick = { onItemClick(camera) },
            )
        }
    }
}