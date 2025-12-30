package com.jsontextfield.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.core.R
import com.jsontextfield.core.ui.FilterMode

@Composable
fun FilterChipStrip(
    enabled: Boolean = true,
    filterMode: FilterMode,
    onChangeFilterMode: (FilterMode) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            start = WindowInsets.safeDrawing.asPaddingValues().calculateStartPadding(
                LayoutDirection.Ltr
            ) + 12.dp, end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(
                LayoutDirection.Ltr
            ) + 12.dp
        )
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