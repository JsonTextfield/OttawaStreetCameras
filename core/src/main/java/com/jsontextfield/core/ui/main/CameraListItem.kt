package com.jsontextfield.core.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jsontextfield.core.R
import com.jsontextfield.core.entities.BilingualObject
import com.jsontextfield.core.entities.Camera
import com.jsontextfield.core.ui.theme.favourite

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraListItem(
    camera: Camera,
    modifier: Modifier = Modifier,
    showDistance: Boolean = false,
    onClick: (Camera) -> Unit = {},
    onLongClick: (Camera) -> Unit = {},
    onFavouriteClick: (Camera) -> Unit = {},
) {
    Row(
        modifier = modifier
            .heightIn(min = 80.dp)
            .combinedClickable(
                onClick = { onClick(camera) },
                onLongClick = { onLongClick(camera) }
            )
            .fillMaxWidth()
            .background(
                if (camera.isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                }
                else {
                    ListItemDefaults.containerColor
                }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (showDistance) {
            Text(
                text = camera.distanceString,
                style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = camera.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
            )
            if (camera.neighbourhood.isNotBlank()) {
                Text(
                    text = camera.neighbourhood,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                )
            }
        }

        IconButton(
            onClick = { onFavouriteClick(camera) }
        ) {
            if (camera.isFavourite) {
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = stringResource(R.string.remove_from_favourites),
                    tint = favourite
                )
            }
            else {
                Icon(
                    Icons.Rounded.StarBorder,
                    contentDescription = stringResource(R.string.add_to_favourites),
                )
            }
        }
    }
}

@Preview
@Composable
private fun CameraListItemPreview() {
    CameraListItem(
        Camera(
            _name = BilingualObject("Name", "Name"),
            _neighbourhood = BilingualObject("Neighbourhood", "Neighbourhood"),
        )
    )
}