package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.textfield.json.ottawastreetcameras.entities.Camera


@Composable
fun StreetCamsMap(cameras: List<Camera>, onItemClick: (Marker) -> Unit) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(
                45.4,
                -75.7
            ), 10f
        )
    }
    val bounds = LatLngBounds.builder()
    for (camera in cameras) {
        bounds.include(LatLng(camera.lat, camera.lon))
    }
    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(latLngBoundsForCameraTarget = bounds.build()),
        onMapLoaded = {
            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50))
        }
    ) {
        cameras.map { camera ->
            Marker(
                state = MarkerState(position = LatLng(camera.lat, camera.lon)),
                tag = camera,
                title = camera.getName(),
                onInfoWindowClick = onItemClick
            )
        }
    }
}