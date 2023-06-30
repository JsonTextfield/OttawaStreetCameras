package com.textfield.json.ottawastreetcameras.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun ActionBar(onItemSelected: (id: Int) -> Unit) {
    val cameraManager = CameraManager.getInstance()
    val width = LocalConfiguration.current.screenWidthDp
    val maxActions = width / 48 / 3
    var remainingActions = maxActions
    Log.e("WIDTH", width.toString())
    Log.e("MAX_ACTIONS", remainingActions.toString())

    val showMenuOptions = hashMapOf(
        R.string.gallery to (remainingActions-- > 0),
        R.string.sort to (cameraManager.viewMode.value != ViewMode.MAP && remainingActions-- > 0),
        R.string.search to (remainingActions-- > 0),
        R.string.search_neighbourhood to (remainingActions-- > 0),
        R.string.favourites to (remainingActions-- > 0),
        R.string.hidden_cameras to (remainingActions-- > 0),
        R.string.random_camera to (remainingActions-- > 0),
        R.string.shuffle to (remainingActions-- > 0),
        R.string.about to (remainingActions-- > 0),
    )
    Box {
        var showViewModeMenu by remember { mutableStateOf(false) }
        ViewModeMenu(showViewModeMenu) {
            showViewModeMenu = false
            onItemSelected(R.string.gallery)
        }
        MenuItem(
            icon = when (cameraManager.viewMode.value) {
                ViewMode.LIST -> {
                    Icons.Rounded.List
                }

                ViewMode.MAP -> {
                    Icons.Filled.Place
                }

                else -> {
                    Icons.Rounded.GridView
                }
            },
            tooltip = when (cameraManager.viewMode.value) {
                ViewMode.LIST -> {
                    stringResource(R.string.list)
                }

                ViewMode.MAP -> {
                    stringResource(R.string.map)
                }

                else -> {
                    stringResource(R.string.gallery)
                }
            },
            visible = showMenuOptions[R.string.gallery] ?: false
        ) {
            showViewModeMenu = !showViewModeMenu
            onItemSelected(R.string.gallery)
        }
    }
    Box {
        var showSortMenu by remember { mutableStateOf(false) }
        SortModeMenu(showSortMenu) {
            showSortMenu = false
            onItemSelected(R.string.sort)
        }

        MenuItem(
            icon = Icons.Rounded.Sort,
            tooltip = stringResource(R.string.sort),
            visible = showMenuOptions[R.string.sort] ?: false
        ) {
            showSortMenu = !showSortMenu
            onItemSelected(R.string.sort)
        }
    }
    MenuItem(
        icon = Icons.Rounded.Search,
        tooltip = stringResource(R.string.search),
        visible = showMenuOptions[R.string.search] ?: false
    ) {
        cameraManager.onSearchModeChanged(SearchMode.NAME)
        onItemSelected(R.string.search)
    }
    MenuItem(
        icon = Icons.Rounded.TravelExplore,
        tooltip = stringResource(R.string.search_neighbourhood),
        visible = showMenuOptions[R.string.neighbourhoods] ?: false
    ) {
        cameraManager.onSearchModeChanged(SearchMode.NEIGHBOURHOOD)
        onItemSelected(R.string.search_neighbourhood)
    }
    MenuItem(
        icon = Icons.Rounded.Star,
        tooltip = stringResource(R.string.favourites),
        visible = showMenuOptions[R.string.favourites] ?: false
    ) {
        cameraManager.onFilterModeChanged(FilterMode.FAVOURITE)
        onItemSelected(R.string.favourites)
    }
    MenuItem(
        icon = Icons.Rounded.VisibilityOff,
        tooltip = stringResource(R.string.hidden_cameras),
        visible = showMenuOptions[R.string.hidden_cameras] ?: false
    ) {
        cameraManager.onFilterModeChanged(FilterMode.HIDDEN)
        onItemSelected(R.string.hide)
    }
    MenuItem(
        icon = Icons.Rounded.Casino,
        tooltip = stringResource(R.string.random_camera),
        visible = showMenuOptions[R.string.random_camera] ?: false
    ) {
        onItemSelected(R.string.random_camera)
    }
    MenuItem(
        icon = Icons.Rounded.Shuffle,
        tooltip = stringResource(R.string.shuffle),
        visible = showMenuOptions[R.string.shuffle] ?: false
    ) {
        onItemSelected(R.string.shuffle)
    }
    MenuItem(
        icon = Icons.Rounded.Info,
        tooltip = stringResource(R.string.about),
        visible = showMenuOptions[R.string.about] ?: false
    ) {
        onItemSelected(R.string.about)
    }
    if (remainingActions < 0) {

        Box {
            var showOverflowMenu by remember { mutableStateOf(false) }
            OverflowMenu(showOverflowMenu, showMenuOptions) {
                showOverflowMenu = false
                onItemSelected(it)
            }
            MenuItem(
                icon = Icons.Rounded.MoreVert,
                tooltip = stringResource(R.string.more),
                visible = true
            ) {
                showOverflowMenu = true
            }
        }
    }

}