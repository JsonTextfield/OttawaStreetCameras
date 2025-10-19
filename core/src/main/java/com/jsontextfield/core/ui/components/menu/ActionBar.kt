package com.jsontextfield.core.ui.components.menu

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jsontextfield.core.R
import com.jsontextfield.core.entities.Camera
import com.jsontextfield.core.ui.SearchMode
import com.jsontextfield.core.ui.SortMode
import com.jsontextfield.core.ui.ThemeMode
import com.jsontextfield.core.ui.ViewMode
import com.jsontextfield.core.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun ActionBar(
    maxActions: Int,
    actions: List<Action> = emptyList(),
) {
    val visibleActions = actions.filter { it.isVisible }

    val displayActions = visibleActions.take(maxActions)
    displayActions.forEach { action ->
        var showMenu by remember { mutableStateOf(false) }
        if (action.menuContent != null) {
            PopupMenu(
                showMenu = showMenu,
                menuContent = action.menuContent,
            )
        }
        MenuItem(
            icon = action.icon,
            tooltip = action.tooltip,
            onClick = {
                if (action.menuContent != null) {
                    showMenu = !showMenu
                }
                else {
                    action.onClick()
                }
            }
        )
    }

    val overflowActions = visibleActions.drop(maxActions)
    if (overflowActions.isNotEmpty()) {
        Box {
            var showOverflowMenu by remember { mutableStateOf(false) }
            OverflowMenu(showOverflowMenu, overflowActions) {
                showOverflowMenu = false
            }
            MenuItem(
                icon = R.drawable.rounded_more_vert_24,
                tooltip = stringResource(R.string.more),
                onClick = {
                    showOverflowMenu = true
                }
            )
        }
    }
}

@Composable
fun getActions(
    mainViewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,
    onNavigateToCameraScreen: (List<Camera>, Boolean) -> Unit = { _, _ -> }
): List<Action> {
    val cameraState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val selectedCameras = cameraState.selectedCameras
    val context = LocalContext.current
    val clearSelection = Action(
        icon = R.drawable.round_clear_24, tooltip = stringResource(R.string.clear), true,
        onClick = { mainViewModel.selectAllCameras(false) },
    )
    val view = Action(
        icon = R.drawable.round_photo_camera_24,
        tooltip = stringResource(id = R.string.view),
        isVisible = selectedCameras.size <= 8,
        onClick = { onNavigateToCameraScreen(cameraState.selectedCameras, false) },
    )
    val allIsFavourite = selectedCameras.all { it.isFavourite }
    val favourite = Action(
        icon = if (allIsFavourite) R.drawable.round_star_border_24 else R.drawable.round_star_24,
        tooltip = stringResource(
            if (allIsFavourite) {
                R.string.remove_from_favourites
            }
            else {
                R.string.add_to_favourites
            }
        ),
        onClick = { mainViewModel.favouriteCameras(cameraState.selectedCameras) },
    )
    val allIsHidden = selectedCameras.all { !it.isVisible }
    val hide = Action(
        icon = if (allIsHidden) R.drawable.round_visibility_24 else R.drawable.round_visibility_off_24,
        tooltip = stringResource(if (allIsHidden) R.string.unhide else R.string.hide),
        onClick = { mainViewModel.hideCameras(cameraState.selectedCameras) },
    )
    val selectAll = Action(
        icon = R.drawable.rounded_select_all_24,
        tooltip = stringResource(R.string.select_all),
        isVisible = selectedCameras.size < cameraState.getDisplayedCameras(searchText = mainViewModel.searchText).size,
        onClick = { mainViewModel.selectAllCameras() },
    )
    val switchView = Action(
        icon = when (cameraState.viewMode) {
            ViewMode.LIST -> R.drawable.rounded_list_24
            ViewMode.MAP -> R.drawable.baseline_place_24
            ViewMode.GALLERY -> R.drawable.round_grid_view_24
        },
        tooltip = stringResource(
            id = when (cameraState.viewMode) {
                ViewMode.LIST -> R.string.list
                ViewMode.MAP -> R.string.map
                ViewMode.GALLERY -> R.string.gallery
            }
        ),
        menuContent = {
            var isExpanded by remember { mutableStateOf(it) }
            DropdownMenu(
                expanded = isExpanded xor it,
                onDismissRequest = { isExpanded = !isExpanded },
            ) {
                ViewMode.entries.forEach { viewMode ->
                    RadioMenuItem(
                        title = stringResource(viewMode.key),
                        isSelected = cameraState.viewMode == viewMode,
                        onClick = {
                            isExpanded = !isExpanded
                            mainViewModel.changeViewMode(viewMode)
                        },
                    )
                }
            }
        },
    )
    val sort = Action(
        icon = R.drawable.rounded_sort_24,
        isVisible = cameraState.viewMode != ViewMode.MAP,
        tooltip = stringResource(id = R.string.sort),
        menuContent = {
            val permissionArray = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            val scope = rememberCoroutineScope()
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { permissions ->
                    val noPermissionsGranted = permissionArray.all { permission ->
                        ContextCompat.checkSelfPermission(
                            context,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    }

                    if (!noPermissionsGranted) {
                        val locationManager =
                            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val lastLocation =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (lastLocation != null) {
                            mainViewModel.changeSortMode(SortMode.DISTANCE, lastLocation)
                        }
                        else {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.location_unavailable))
                            }
                        }
                    }
                },
            )

            var isExpanded by remember { mutableStateOf(it) }
            DropdownMenu(
                expanded = isExpanded xor it,
                onDismissRequest = { isExpanded = !isExpanded },
            ) {
                SortMode.entries.forEach { sortMode ->
                    RadioMenuItem(
                        title = stringResource(sortMode.key),
                        isSelected = cameraState.sortMode == sortMode,
                        onClick = {
                            isExpanded = !isExpanded
                            if (sortMode == SortMode.DISTANCE) {
                                locationPermissionLauncher.launch(permissionArray)
                            }
                            else {
                                mainViewModel.changeSortMode(sortMode)
                            }
                        },
                    )
                }
            }
        },
    )
    val search = Action(
        icon = R.drawable.rounded_search_24,
        isVisible = cameraState.searchMode != SearchMode.NAME,
        tooltip = stringResource(id = R.string.search),
        onClick = { mainViewModel.searchCameras(SearchMode.NAME) },
    )
    val searchNeighbourhood = Action(
        icon = R.drawable.rounded_travel_explore_24,
        isVisible = cameraState.showSearchNeighbourhood,
        tooltip = stringResource(id = R.string.search_neighbourhood),
        onClick = { mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD) },
    )
    val random = Action(
        icon = R.drawable.round_casino_24,
        tooltip = stringResource(id = R.string.random_camera),
        onClick = {
            onNavigateToCameraScreen(listOf(cameraState.visibleCameras.random()), false)
        },
    )
    val shuffle = Action(
        icon = R.drawable.rounded_shuffle_24,
        tooltip = stringResource(id = R.string.shuffle),
        onClick = {
            onNavigateToCameraScreen(emptyList(), true)
        },
    )
    val theme by mainViewModel.theme.collectAsStateWithLifecycle()
    val darkMode = Action(
        icon = R.drawable.round_brightness_medium_24,
        tooltip = stringResource(R.string.change_theme),
        menuContent = {
            var isExpanded by remember { mutableStateOf(it) }
            DropdownMenu(
                expanded = isExpanded xor it,
                onDismissRequest = { isExpanded = !isExpanded },
            ) {
                ThemeMode.entries.forEach { themeMode ->
                    RadioMenuItem(
                        title = stringResource(themeMode.key),
                        isSelected = theme == themeMode,
                        onClick = {
                            isExpanded = !isExpanded
                            mainViewModel.changeTheme(themeMode)
                        },
                    )
                }
            }
        },
    )

    if (selectedCameras.isEmpty()) {
        return listOf(
            switchView,
            sort,
            search,
            searchNeighbourhood,
            random,
            shuffle,
            darkMode,
        )
    }
    return listOf(
        clearSelection,
        view,
        favourite,
        hide,
        selectAll,
    )
}