package com.textfield.json.ottawastreetcameras.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.DropdownMenu
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
fun OverflowMenu(expanded: Boolean, onItemSelected: (id: Int) -> Unit) {
    val width = LocalConfiguration.current.screenWidthDp
    var remainingActions = width / 48 / 2 - 1
    val cameraManager = CameraManager.getInstance()

    Log.e("WIDTH", width.toString())
    Log.e("REMAINING_ACTIONS", remainingActions.toString())

    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected(-1) }) {

        Box {
            var showViewModeMenu by remember { mutableStateOf(false) }
            ViewModeMenu(showViewModeMenu) {
                showViewModeMenu = false
            }
            OverflowMenuItem(
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
                tooltip = stringResource(R.string.list),
                visible = cameraManager.viewMode.value != ViewMode.LIST && remainingActions-- < 1
            ) {
                showViewModeMenu = !showViewModeMenu
                onItemSelected(R.string.list)
            }
        }

        Box {

            var showSortMenu by remember { mutableStateOf(false) }
            SortModeMenu(showSortMenu) {
                showSortMenu = false
            }

            OverflowMenuItem(
                icon = Icons.Rounded.Sort,
                tooltip = stringResource(R.string.sort),
                visible = cameraManager.viewMode.value != ViewMode.MAP && remainingActions-- < 1
            ) {
                showSortMenu = !showSortMenu
                onItemSelected(R.string.sort)
            }
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Search,
            tooltip = stringResource(R.string.search),
            visible = remainingActions-- < 1
        ) {
            cameraManager.onSearchModeChanged(SearchMode.NAME)
            onItemSelected(R.string.search)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.TravelExplore,
            tooltip = stringResource(R.string.search_neighbourhood),
            visible = remainingActions-- < 1
        ) {
            cameraManager.onSearchModeChanged(SearchMode.NEIGHBOURHOOD)
            onItemSelected(R.string.search_neighbourhood)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Star,
            tooltip = stringResource(R.string.favourites),
            visible = remainingActions-- < 1
        ) {
            cameraManager.onFilterModeChanged(FilterMode.FAVOURITE)
            onItemSelected(R.string.favourites)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.VisibilityOff,
            tooltip = stringResource(R.string.hidden_cameras),
            visible = remainingActions-- < 1
        ) {
            cameraManager.onFilterModeChanged(FilterMode.HIDDEN)
            onItemSelected(R.string.hidden_cameras)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Casino,
            tooltip = stringResource(R.string.random_camera),
            visible = remainingActions-- < 1
        ) {
            onItemSelected(R.string.random_camera)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Shuffle,
            tooltip = stringResource(R.string.shuffle),
            visible = remainingActions-- < 1
        ) {
            onItemSelected(R.string.shuffle)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Info,
            tooltip = stringResource(R.string.about),
            visible = remainingActions-- < 1
        ) {
            onItemSelected(R.string.about)
        }
    }
}