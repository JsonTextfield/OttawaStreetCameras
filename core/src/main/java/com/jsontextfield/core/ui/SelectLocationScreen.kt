package com.jsontextfield.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.core.R
import com.jsontextfield.core.entities.City

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLocationScreen(
    selectedCity: City,
    onCitySelected: (City) -> Unit,
    onBackPressed: () -> Unit,
) {
    val gridState = rememberLazyGridState()
    LaunchedEffect(Unit) {
        gridState.scrollToItem(selectedCity.ordinal)
    }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.change_location))
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(painterResource(R.drawable.round_arrow_back_24), null)
                }
            }
        )
    }) {
        val density = LocalDensity.current
        val widthDp =
            (LocalWindowInfo.current.containerSize.width / density.density - WindowInsets.safeDrawing.asPaddingValues()
                .calculateLeftPadding(
                    LayoutDirection.Ltr
                ).value - WindowInsets.safeDrawing.asPaddingValues()
                .calculateRightPadding(LayoutDirection.Ltr).value).toInt()
        val columns = (widthDp / 200).coerceIn(1, 4)
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
                .semantics {
                    collectionInfo = CollectionInfo(
                        rowCount = City.entries.size,
                        columnCount = columns,
                    )
                },
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(bottom = 100.dp),
            state = gridState,
        ) {
            itemsIndexed(City.entries) { index, city ->
                Text(
                    text = stringResource(city.stringRes),
                    modifier = Modifier
                        .background(
                            color = if (city == selectedCity) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.background
                            }
                        )
                        .clickable {
                            onCitySelected(city)
                        }
                        .padding(12.dp)
                        .padding(
                            start = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateStartPadding(LayoutDirection.Ltr),
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(LayoutDirection.Ltr),
                        )
                        .semantics {
                            collectionItemInfo = CollectionItemInfo(
                                rowIndex = index / columns,
                                columnIndex = index % columns,
                                rowSpan = 1,
                                columnSpan = 1,
                            )
                        }
                )
            }
        }
    }
}