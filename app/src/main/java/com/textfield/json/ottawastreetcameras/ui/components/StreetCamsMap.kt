package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
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
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun StreetCamsMap(cameras: List<Camera>, isMyLocationEnabled: Boolean, onItemClick: (Camera) -> Unit) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.builder().target(LatLng(45.45, -75.69)).build()
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
            } else {
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
                onInfoWindowClick = {
                    if (CameraManager.getInstance().getSelectedCameras().isNotEmpty()) {
                        CameraManager.getInstance().selectCamera(camera)
                    } else {
                        onItemClick(camera)
                    }
                },
                onInfoWindowLongClick = {
                    CameraManager.getInstance().selectCamera(camera)
                },
                icon = if (camera.isFavourite) {
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                } else {
                    BitmapDescriptorFactory.defaultMarker()
                }
            )
        }
    }
}