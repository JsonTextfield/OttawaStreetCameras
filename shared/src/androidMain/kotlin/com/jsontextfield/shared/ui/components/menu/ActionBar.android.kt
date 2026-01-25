package com.jsontextfield.shared.ui.components.menu

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.material3.SnackbarHostState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jsontextfield.shared.ui.SortMode
import com.jsontextfield.shared.ui.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.core.context.GlobalContext
import streetcams.shared.generated.resources.Res
import streetcams.shared.generated.resources.location_unavailable


actual fun sortByDistance(
    mainViewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val context = GlobalContext.get().get<Context>()
    val scope = CoroutineScope(Dispatchers.Main)
    val permissionArray = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    val noPermissionsGranted = permissionArray.all { permission ->
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) != PackageManager.PERMISSION_GRANTED
    }

    if (!noPermissionsGranted) {
        // Permissions already granted, proceed with location
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastLocation =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (lastLocation != null) {
            mainViewModel.changeSortMode(SortMode.DISTANCE, lastLocation.latitude to lastLocation.longitude)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(getString(Res.string.location_unavailable))
            }
        }
    } else {
        // Request permissions from the activity
        ActivityCompat.requestPermissions(
            context as Activity,
            permissionArray,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}

private const val LOCATION_PERMISSION_REQUEST_CODE = 100
