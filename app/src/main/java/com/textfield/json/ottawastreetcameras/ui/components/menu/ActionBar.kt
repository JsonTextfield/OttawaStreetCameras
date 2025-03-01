package com.textfield.json.ottawastreetcameras.ui.components.menu

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.entities.Camera
import com.textfield.json.ottawastreetcameras.ui.components.AboutDialog
import com.textfield.json.ottawastreetcameras.ui.main.FilterMode
import com.textfield.json.ottawastreetcameras.ui.main.MainViewModel
import com.textfield.json.ottawastreetcameras.ui.main.SearchMode
import com.textfield.json.ottawastreetcameras.ui.main.SortMode
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import kotlinx.coroutines.launch

@Composable
fun ActionBar(
    actions: List<Action> = emptyList(),
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val maxActions = when {
        screenWidthDp < 400 -> screenWidthDp / 4 / 48
        screenWidthDp < 600 -> screenWidthDp / 3 / 48
        screenWidthDp < 800 -> screenWidthDp / 2 / 48
        else -> screenWidthDp * 2 / 3 / 48
    }

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
                icon = Icons.Rounded.MoreVert,
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
    val cameraState by mainViewModel.cameraState.collectAsStateWithLifecycle()
    val selectedCameras = cameraState.selectedCameras
    val context = LocalContext.current
    val clearSelection = Action(
        icon = Icons.Rounded.Clear, tooltip = stringResource(R.string.clear), true,
        onClick = { mainViewModel.selectAllCameras(false) },
    )
    val view = Action(
        icon = Icons.Rounded.CameraAlt,
        tooltip = stringResource(id = R.string.view),
        selectedCameras.size <= 8,
        onClick = { onNavigateToCameraScreen(cameraState.selectedCameras, false) },
    )
    val allIsFavourite = selectedCameras.all { it.isFavourite }
    val favourite = Action(
        icon = if (allIsFavourite) Icons.Rounded.StarBorder else Icons.Rounded.Star,
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
        icon = if (allIsHidden) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
        tooltip = stringResource(if (allIsHidden) R.string.unhide else R.string.hide),
        onClick = { mainViewModel.hideCameras(cameraState.selectedCameras) },
    )
    val selectAll = Action(
        icon = Icons.Rounded.SelectAll,
        tooltip = stringResource(R.string.select_all),
        isVisible = selectedCameras.size < cameraState.displayedCameras.size,
        onClick = { mainViewModel.selectAllCameras() },
    )
    val switchView = Action(
        icon = when (cameraState.viewMode) {
            ViewMode.LIST -> Icons.AutoMirrored.Rounded.List
            ViewMode.MAP -> Icons.Filled.Place
            ViewMode.GALLERY -> Icons.Rounded.GridView
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
        icon = Icons.AutoMirrored.Rounded.Sort,
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
        icon = Icons.Rounded.Search,
        isVisible = cameraState.searchMode != SearchMode.NAME,
        tooltip = stringResource(id = R.string.search),
        isChecked = cameraState.searchMode == SearchMode.NAME,
        onClick = { mainViewModel.searchCameras(SearchMode.NAME) },
    )
    val searchNeighbourhood = Action(
        icon = Icons.Rounded.TravelExplore,
        isVisible = cameraState.showSearchNeighbourhood,
        tooltip = stringResource(id = R.string.search_neighbourhood),
        isChecked = cameraState.searchMode == SearchMode.NEIGHBOURHOOD,
        onClick = { mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD) },
    )
    val favourites = Action(
        icon = Icons.Rounded.Star,
        tooltip = stringResource(id = R.string.favourites),
        isChecked = cameraState.filterMode == FilterMode.FAVOURITE,
        onClick = { mainViewModel.changeFilterMode(FilterMode.FAVOURITE) },
    )
    val hidden = Action(
        icon = Icons.Rounded.VisibilityOff,
        tooltip = stringResource(id = R.string.hidden_cameras),
        isChecked = cameraState.filterMode == FilterMode.HIDDEN,
        onClick = { mainViewModel.changeFilterMode(FilterMode.HIDDEN) },
    )
    val random = Action(
        icon = Icons.Rounded.Casino,
        tooltip = stringResource(id = R.string.random_camera),
        onClick = {
            onNavigateToCameraScreen(listOf(cameraState.visibleCameras.random()), false)
        },
    )
    val shuffle = Action(
        icon = Icons.Rounded.Shuffle,
        tooltip = stringResource(id = R.string.shuffle),
        onClick = {
            onNavigateToCameraScreen(emptyList(), true)
        },
    )
    val theme by mainViewModel.theme.collectAsStateWithLifecycle()
    val darkMode = Action(
        icon = Icons.Rounded.BrightnessMedium,
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

    var showAboutDialog by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        AboutDialog(
            onLicences = {
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            },
            onDismiss = { showAboutDialog = false },
        )
    }

    val about = Action(
        icon = Icons.Rounded.Info,
        tooltip = stringResource(id = R.string.about),
        onClick = { showAboutDialog = true },
    )

    if (selectedCameras.isEmpty()) {
        return listOf(
            switchView,
            sort,
            search,
            searchNeighbourhood,
            favourites,
            hidden,
            random,
            shuffle,
            darkMode,
            about,
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