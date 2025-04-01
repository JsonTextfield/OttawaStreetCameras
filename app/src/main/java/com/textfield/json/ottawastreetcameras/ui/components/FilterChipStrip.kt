package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ui.main.FilterMode

@Composable
fun FilterChipStrip(
    filterMode: FilterMode,
    onChangeFilterMode: (FilterMode) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(4.dp),
    ) {
        ElevatedFilterChip(
            selected = filterMode == FilterMode.FAVOURITE,
            onClick = { onChangeFilterMode(FilterMode.FAVOURITE) },
            label = {
                Text(stringResource(R.string.favourites))
            },
            leadingIcon = {
                Icon(Icons.Rounded.Star, contentDescription = null)
            }
        )
        ElevatedFilterChip(
            selected = filterMode == FilterMode.HIDDEN,
            onClick = { onChangeFilterMode(FilterMode.HIDDEN) },
            label = {
                Text(stringResource(R.string.hidden_cameras))
            },
            leadingIcon = {
                Icon(Icons.Rounded.VisibilityOff, contentDescription = null)
            }
        )
    }
}