package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.textfield.json.ottawastreetcameras.ui.main.FilterMode

@Composable
fun FilterChipStrip(
    filterMode: FilterMode,
    onChangeFilterMode: (FilterMode) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(4.dp),
    ) {
        items(FilterMode.entries) {
            FilterChip(
                selected = filterMode == it,
                onClick = { onChangeFilterMode(it) },
                label = {
                    Text(stringResource(it.key))
                },
            )
        }
    }
}