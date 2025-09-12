package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.ui.main.FilterMode

@Composable
fun FilterChipStrip(
    enabled: Boolean = true,
    filterMode: FilterMode,
    onChangeFilterMode: (FilterMode) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        item {
            ElevatedFilterChip(
                selected = filterMode == FilterMode.VISIBLE,
                onClick = { onChangeFilterMode(FilterMode.VISIBLE) },
                label = {
                    Text(stringResource(R.string.all))
                },
                enabled = enabled,
            )
        }

        item {
            ElevatedFilterChip(
                selected = filterMode == FilterMode.FAVOURITE,
                onClick = { onChangeFilterMode(FilterMode.FAVOURITE) },
                label = {
                    Text(stringResource(R.string.favourites))
                },
                enabled = enabled,
            )
        }

        item {
            ElevatedFilterChip(
                selected = filterMode == FilterMode.HIDDEN,
                onClick = { onChangeFilterMode(FilterMode.HIDDEN) },
                label = {
                    Text(stringResource(R.string.hidden_cameras))
                },
                enabled = enabled,
            )
        }
    }
}