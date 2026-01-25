package com.jsontextfield.shared.ui.components.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jsontextfield.shared.entities.Camera
import com.jsontextfield.shared.ui.SearchMode
import com.jsontextfield.shared.ui.SortMode
import com.jsontextfield.shared.ui.ThemeMode
import com.jsontextfield.shared.ui.ViewMode
import com.jsontextfield.shared.ui.viewmodels.MainViewModel
import org.jetbrains.compose.resources.stringResource
import streetcams.shared.generated.resources.Res
import streetcams.shared.generated.resources.add_to_favourites
import streetcams.shared.generated.resources.change_theme
import streetcams.shared.generated.resources.clear
import streetcams.shared.generated.resources.gallery
import streetcams.shared.generated.resources.hide
import streetcams.shared.generated.resources.list
import streetcams.shared.generated.resources.map
import streetcams.shared.generated.resources.more
import streetcams.shared.generated.resources.random_camera
import streetcams.shared.generated.resources.remove_from_favourites
import streetcams.shared.generated.resources.round_brightness_medium_24
import streetcams.shared.generated.resources.round_casino_24
import streetcams.shared.generated.resources.round_clear_24
import streetcams.shared.generated.resources.round_grid_view_24
import streetcams.shared.generated.resources.round_list_24
import streetcams.shared.generated.resources.round_more_vert_24
import streetcams.shared.generated.resources.round_photo_camera_24
import streetcams.shared.generated.resources.round_place_24
import streetcams.shared.generated.resources.round_search_24
import streetcams.shared.generated.resources.round_select_all_24
import streetcams.shared.generated.resources.round_shuffle_24
import streetcams.shared.generated.resources.round_sort_24
import streetcams.shared.generated.resources.round_star_24
import streetcams.shared.generated.resources.round_star_border_24
import streetcams.shared.generated.resources.round_travel_explore_24
import streetcams.shared.generated.resources.round_visibility_24
import streetcams.shared.generated.resources.round_visibility_off_24
import streetcams.shared.generated.resources.search
import streetcams.shared.generated.resources.search_neighbourhood
import streetcams.shared.generated.resources.select_all
import streetcams.shared.generated.resources.shuffle
import streetcams.shared.generated.resources.sort
import streetcams.shared.generated.resources.unhide
import streetcams.shared.generated.resources.view

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
                icon = Res.drawable.round_more_vert_24,
                tooltip = stringResource(Res.string.more),
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
    val cameraState by mainViewModel.uiState.collectAsState()
    val selectedCameras = cameraState.selectedCameras
    val clearSelection = Action(
        icon = Res.drawable.round_clear_24, tooltip = stringResource(Res.string.clear), true,
        onClick = { mainViewModel.selectAllCameras(false) },
    )
    val view = Action(
        icon = Res.drawable.round_photo_camera_24,
        tooltip = stringResource(Res.string.view),
        isVisible = selectedCameras.size <= 8,
        onClick = { onNavigateToCameraScreen(cameraState.selectedCameras, false) },
    )
    val allIsFavourite = selectedCameras.all { it.isFavourite }
    val favourite = Action(
        icon = if (allIsFavourite) Res.drawable.round_star_border_24 else Res.drawable.round_star_24,
        tooltip = stringResource(
            if (allIsFavourite) {
                Res.string.remove_from_favourites
            }
            else {
                Res.string.add_to_favourites
            }
        ),
        onClick = { mainViewModel.favouriteCameras(cameraState.selectedCameras) },
    )
    val allIsHidden = selectedCameras.all { !it.isVisible }
    val hide = Action(
        icon = if (allIsHidden) Res.drawable.round_visibility_24 else Res.drawable.round_visibility_off_24,
        tooltip = stringResource(if (allIsHidden) Res.string.unhide else Res.string.hide),
        onClick = { mainViewModel.hideCameras(cameraState.selectedCameras) },
    )
    val selectAll = Action(
        icon = Res.drawable.round_select_all_24,
        tooltip = stringResource(Res.string.select_all),
        isVisible = selectedCameras.size < cameraState.getDisplayedCameras(searchText = mainViewModel.searchText).size,
        onClick = { mainViewModel.selectAllCameras() },
    )
    val switchView = Action(
        icon = when (cameraState.viewMode) {
            ViewMode.LIST -> Res.drawable.round_list_24
            ViewMode.MAP -> Res.drawable.round_place_24
            ViewMode.GALLERY -> Res.drawable.round_grid_view_24
        },
        tooltip = stringResource(
            when (cameraState.viewMode) {
                ViewMode.LIST -> Res.string.list
                ViewMode.MAP -> Res.string.map
                ViewMode.GALLERY -> Res.string.gallery
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
        icon = Res.drawable.round_sort_24,
        isVisible = cameraState.viewMode != ViewMode.MAP,
        tooltip = stringResource(Res.string.sort),
        menuContent = {
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
                                sortByDistance(mainViewModel, snackbarHostState)
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
        icon = Res.drawable.round_search_24,
        isVisible = cameraState.searchMode != SearchMode.NAME,
        tooltip = stringResource(Res.string.search),
        onClick = { mainViewModel.searchCameras(SearchMode.NAME) },
    )
    val searchNeighbourhood = Action(
        icon = Res.drawable.round_travel_explore_24,
        isVisible = cameraState.showSearchNeighbourhood,
        tooltip = stringResource(Res.string.search_neighbourhood),
        onClick = { mainViewModel.searchCameras(SearchMode.NEIGHBOURHOOD) },
    )
    val random = Action(
        icon = Res.drawable.round_casino_24,
        tooltip = stringResource(Res.string.random_camera),
        onClick = {
            onNavigateToCameraScreen(listOf(cameraState.visibleCameras.random()), false)
        },
    )
    val shuffle = Action(
        icon = Res.drawable.round_shuffle_24,
        tooltip = stringResource(Res.string.shuffle),
        onClick = {
            onNavigateToCameraScreen(emptyList(), true)
        },
    )
    val theme by mainViewModel.theme.collectAsState()
    val darkMode = Action(
        icon = Res.drawable.round_brightness_medium_24,
        tooltip = stringResource(Res.string.change_theme),
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

expect fun sortByDistance(mainViewModel: MainViewModel, snackbarHostState: SnackbarHostState)