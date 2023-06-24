package com.textfield.json.ottawastreetcameras.ui

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
import androidx.compose.ui.platform.LocalContext
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun OverflowMenu(expanded: Boolean, onItemSelected: (id: Int) -> Unit) {
    val context = LocalContext.current
    val width = LocalConfiguration.current.screenWidthDp
    var remainingActions = width / 48 / 2 - 1
    var cameraManager = CameraManager.getInstance()

    Log.e("WIDTH", width.toString())
    Log.e("REMAINING_ACTIONS", remainingActions.toString())

    DropdownMenu(expanded = expanded, onDismissRequest = { onItemSelected(-1) }) {

        Box() {
            var showViewModeMenu by remember { mutableStateOf(false) }
            ViewModeMenu(showViewModeMenu) {
                showViewModeMenu = false
            }
            OverflowMenuItem(
                icon = when (cameraManager.viewMode) {
                    ViewMode.LIST -> {
                        Icons.Rounded.List
                    }

                    ViewMode.MAP -> {
                        Icons.Filled.Place
                    }

                    ViewMode.GALLERY -> {
                        Icons.Rounded.GridView
                    }
                },
                tooltip = context.getString(R.string.list),
                visible = cameraManager.viewMode != ViewMode.LIST && remainingActions-- < 1
            ) {
                showViewModeMenu = !showViewModeMenu
                onItemSelected(R.string.list)
            }
        }

        Box() {

            var showSortMenu by remember { mutableStateOf(false) }
            SortModeMenu(showSortMenu) {
                showSortMenu = false
            }

            OverflowMenuItem(
                icon = Icons.Rounded.Sort,
                tooltip = context.getString(R.string.sort),
                visible = cameraManager.viewMode != ViewMode.MAP && remainingActions-- < 1
            ) {
                showSortMenu = !showSortMenu
                onItemSelected(R.string.sort)
            }
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Search,
            tooltip = context.getString(R.string.search),
            visible = remainingActions-- < 1
        ) {
            cameraManager.searchMode =
                if (cameraManager.searchMode != SearchMode.NAME) SearchMode.NAME else SearchMode.NONE
            onItemSelected(R.string.search)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.TravelExplore,
            tooltip = context.getString(R.string.search_neighbourhood),
            visible = remainingActions-- < 1
        ) {
            cameraManager.searchMode =
                if (cameraManager.searchMode != SearchMode.NEIGHBOURHOOD) SearchMode.NEIGHBOURHOOD else SearchMode.NONE
            onItemSelected(R.string.search_neighbourhood)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Star,
            tooltip = context.getString(R.string.favourites),
            visible = remainingActions-- < 1
        ) {
            cameraManager.filterMode =
                if (cameraManager.filterMode == FilterMode.FAVOURITE) FilterMode.VISIBLE else FilterMode.FAVOURITE
            onItemSelected(R.string.favourites)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.VisibilityOff,
            tooltip = context.getString(R.string.hidden_cameras),
            visible = remainingActions-- < 1
        ) {
            cameraManager.filterMode =
                if (cameraManager.filterMode == FilterMode.HIDDEN) FilterMode.VISIBLE else FilterMode.HIDDEN
            onItemSelected(R.string.hidden_cameras)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Casino,
            tooltip = context.getString(R.string.random_camera),
            visible = remainingActions-- < 1
        ) {
            onItemSelected(R.string.random_camera)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Shuffle,
            tooltip = context.getString(R.string.shuffle),
            visible = remainingActions-- < 1
        ) {
            onItemSelected(R.string.shuffle)
        }
        OverflowMenuItem(
            icon = Icons.Rounded.Info,
            tooltip = context.getString(R.string.about),
            visible = remainingActions-- < 1
        ) {
            onItemSelected(R.string.about)
        }
    }
}