package com.textfield.json.ottawastreetcameras.ui

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
import androidx.compose.ui.platform.LocalContext
import com.textfield.json.ottawastreetcameras.CameraManager
import com.textfield.json.ottawastreetcameras.FilterMode
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.SearchMode
import com.textfield.json.ottawastreetcameras.ViewMode

@Composable
fun ActionBar(onItemSelected: (id: Int) -> Unit) {
    val context = LocalContext.current
    val cameraManager = CameraManager.getInstance()
    val width = LocalConfiguration.current.screenWidthDp
    var remainingActions = width / 48 / 2
    Log.e("WIDTH", width.toString())
    Log.e("MAX_ACTIONS", remainingActions.toString())

    Box() {
        var showViewModeMenu by remember { mutableStateOf(false) }
        ViewModeMenu(showViewModeMenu) {
            showViewModeMenu = false
            onItemSelected(R.string.gallery)
        }
        MenuItem(
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
            tooltip = when (cameraManager.viewMode) {
                ViewMode.LIST -> {
                    context.getString(R.string.list)
                }

                ViewMode.MAP -> {
                    context.getString(R.string.map)
                }

                ViewMode.GALLERY -> {
                    context.getString(R.string.gallery)
                }
            },
            visible = remainingActions-- > 0
        ) {
            showViewModeMenu = !showViewModeMenu
            onItemSelected(R.string.gallery)
        }
    }

    Box() {
        var showSortMenu by remember { mutableStateOf(false) }
        SortModeMenu(showSortMenu) {
            showSortMenu = false
            onItemSelected(R.string.sort)
        }

        MenuItem(
            icon = Icons.Rounded.Sort,
            tooltip = context.getString(R.string.sort),
            visible = cameraManager.viewMode != ViewMode.MAP && remainingActions-- > 0
        ) {
            showSortMenu = !showSortMenu
            onItemSelected(R.string.sort)
        }
    }
    MenuItem(
        icon = Icons.Rounded.Search,
        tooltip = context.getString(R.string.search),
        visible = remainingActions-- > 0
    ) {

        cameraManager.searchMode =
            if (cameraManager.searchMode != SearchMode.NAME) SearchMode.NAME else SearchMode.NONE
        onItemSelected(R.string.search)
    }
    MenuItem(
        icon = Icons.Rounded.TravelExplore,
        tooltip = context.getString(R.string.search_neighbourhood),
        visible = remainingActions-- > 0
    ) {
        cameraManager.searchMode =
            if (cameraManager.searchMode != SearchMode.NEIGHBOURHOOD) SearchMode.NEIGHBOURHOOD else SearchMode.NONE
        onItemSelected(R.string.search_neighbourhood)
    }
    MenuItem(
        icon = Icons.Rounded.Star,
        tooltip = context.getString(R.string.favourites),
        visible = remainingActions-- > 0
    ) {
        cameraManager.filterMode =
            if (cameraManager.filterMode == FilterMode.FAVOURITE) FilterMode.VISIBLE else FilterMode.FAVOURITE
        onItemSelected(R.string.favourites)
    }
    MenuItem(
        icon = Icons.Rounded.VisibilityOff,
        tooltip = context.getString(R.string.hide),
        visible = remainingActions-- > 0
    ) {
        cameraManager.filterMode =
            if (cameraManager.filterMode == FilterMode.HIDDEN) FilterMode.VISIBLE else FilterMode.HIDDEN
        onItemSelected(R.string.hide)
    }
    MenuItem(
        icon = Icons.Rounded.Casino,
        tooltip = context.getString(R.string.random_camera),
        visible = remainingActions-- > 0
    ) {
        onItemSelected(R.string.random_camera)
    }
    MenuItem(
        icon = Icons.Rounded.Shuffle,
        tooltip = context.getString(R.string.shuffle),
        visible = remainingActions-- > 0
    ) {
        //shuffleCameras()
        onItemSelected(R.string.shuffle)
    }
    MenuItem(icon = Icons.Rounded.Info, tooltip = context.getString(R.string.about), visible = remainingActions-- > 0) {
        //showAboutDialog()
        onItemSelected(R.string.about)
    }
    if (remainingActions < 0) {
        Box() {
            var showOverflowMenu by remember { mutableStateOf(false) }
            OverflowMenu(showOverflowMenu) {
                showOverflowMenu = false
            }
            MenuItem(icon = Icons.Rounded.MoreVert, tooltip = context.getString(R.string.more), visible = true) {
                showOverflowMenu = true
                onItemSelected(R.string.more)
            }
        }
    }

}