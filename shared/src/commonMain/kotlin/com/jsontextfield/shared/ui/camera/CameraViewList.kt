package com.jsontextfield.shared.ui.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jsontextfield.shared.entities.Camera

@Composable
fun CameraViewList(
    cameras: List<Camera> = emptyList(),
    displayedCameras: List<Camera> = emptyList(),
    isShuffling: Boolean = false,
    update: Boolean = false,
    onItemLongClick: (Camera) -> Unit = {},
) {
    val verticalScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(verticalScrollState)
            .fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier.height(
                WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
            )
        )
        if (cameras.size == 1 && !isShuffling) {
            val pagerState = rememberPagerState(
                initialPage = displayedCameras.indexOf(cameras.first()),
                pageCount = { displayedCameras.size },
            )
            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
            ) { index ->
                val camera = displayedCameras[index]
                Column(modifier = Modifier.fillMaxSize()) {
                    camera.urls.forEach { url ->
                        CameraView(
                            title = camera.name,
                            url = url,
                            update = update,
                            onLongClick = { onItemLongClick(camera) },
                        )
                    }
                }
            }
        } else {
            cameras.map { camera ->
                Column(modifier = Modifier.fillMaxSize()) {
                    camera.urls.forEach { url ->
                        CameraView(
                            title = camera.name,
                            url = url,
                            update = update,
                            onLongClick = { onItemLongClick(camera) },
                        )
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier.height(
                WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
            )
        )
    }
}