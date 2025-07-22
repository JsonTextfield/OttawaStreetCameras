package com.textfield.json.ottawastreetcameras.ui.main

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
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
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.BilingualObject
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.theme.LocalTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CameraMapView(
    searchText: String,
    cameraState: CameraState,
    isMyLocationEnabled: Boolean = false,
    onItemClick: (Camera) -> Unit = {},
    onItemLongClick: (Camera) -> Unit = {},
) {
    val cameras = cameraState.getDisplayedCameras(searchText)
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
            mapStyleOptions = when (LocalTheme.current) {
                ThemeMode.LIGHT -> null
                ThemeMode.DARK -> MapStyleOptions.loadRawResourceStyle(context, R.raw.dark_mode)
                ThemeMode.SYSTEM -> {
                    if (isSystemInDarkTheme()) {
                        MapStyleOptions.loadRawResourceStyle(context, R.raw.dark_mode)
                    }
                    else {
                        null
                    }
                }
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
        contentPadding = PaddingValues(
            bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding(),
            start = WindowInsets.safeDrawing.asPaddingValues()
                .calculateStartPadding(LayoutDirection.Ltr),
            end = WindowInsets.safeDrawing.asPaddingValues()
                .calculateEndPadding(LayoutDirection.Ltr)
        )
    ) {
        cameras.map { camera ->
            Marker(
                state = MarkerState(position = LatLng(camera.lat, camera.lon)),
                title = camera.name,
                snippet = camera.neighbourhood.ifEmpty { null },
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

@Preview
@Composable
private fun CameraMapViewPreview() {
    val cameraList =
        List(10) { Camera(_name = BilingualObject(en = "Camera $it", fr = "Caméra $it")) }
    CameraMapView("", CameraState(allCameras = cameraList))
}