package com.textfield.json.ottawastreetcameras.ui.components

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
import androidx.compose.ui.res.stringResource
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun OverflowMenu(expanded: Boolean, showMenuOptions: HashMap<Int, Boolean>, onItemSelected: (id: Int) -> Unit) {
    val cameraManager = CameraManager.getInstance()

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onItemSelected(-1) },
    ) {
        Box {
            var showViewModeMenu by remember { mutableStateOf(false) }
            ViewModeMenu(showViewModeMenu) {
                showViewModeMenu = false
                onItemSelected(R.string.gallery)
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
                visible = !(showMenuOptions[R.string.gallery] ?: false)
            ) {
                showViewModeMenu = true
            }
        }
        Box {
            var showSortMenu by remember { mutableStateOf(false) }
            SortModeMenu(showSortMenu) {
                showSortMenu = false
                onItemSelected(R.string.sort)
            }

            OverflowMenuItem(
                icon = Icons.Rounded.Sort,
                tooltip = stringResource(R.string.sort),
                visible = !(cameraManager.viewMode.value != ViewMode.MAP && showMenuOptions[R.string.sort] ?: false)
            ) {
                showSortMenu = !showSortMenu
            }
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Search,
            tooltip = stringResource(R.string.search),
            visible = !(showMenuOptions[R.string.search] ?: false)
        ) {
            cameraManager.onSearchModeChanged(SearchMode.NAME)
            onItemSelected(R.string.search)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.TravelExplore,
            tooltip = stringResource(R.string.search_neighbourhood),
            visible = !(showMenuOptions[R.string.search_neighbourhood] ?: false)
        ) {
            cameraManager.onSearchModeChanged(SearchMode.NEIGHBOURHOOD)
            onItemSelected(R.string.search_neighbourhood)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Star,
            tooltip = stringResource(R.string.favourites),
            visible = !(showMenuOptions[R.string.favourites] ?: false)
        ) {
            cameraManager.onFilterModeChanged(FilterMode.FAVOURITE)
            onItemSelected(R.string.favourites)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.VisibilityOff,
            tooltip = stringResource(R.string.hidden_cameras),
            visible = !(showMenuOptions[R.string.hidden_cameras] ?: false)
        ) {
            cameraManager.onFilterModeChanged(FilterMode.HIDDEN)
            onItemSelected(R.string.hidden_cameras)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Casino,
            tooltip = stringResource(R.string.random_camera),
            visible = !(showMenuOptions[R.string.random_camera] ?: false)
        ) {
            onItemSelected(R.string.random_camera)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Shuffle,
            tooltip = stringResource(R.string.shuffle),
            visible = !(showMenuOptions[R.string.shuffle] ?: false)
        ) {
            onItemSelected(R.string.shuffle)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Info,
            tooltip = stringResource(R.string.about),
            visible = !(showMenuOptions[R.string.about] ?: false)
        ) {
            onItemSelected(R.string.about)
        }
    }
}