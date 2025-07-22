package com.textfield.json.ottawastreetcameras.ui.components

import android.view.MotionEvent
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private fun getIndexData(data: List<String>): LinkedHashMap<String, Int> {
    val dataString = data.map {
        it.toUpperCase(Locale.current).first()
    }.sorted().joinToString("")

    val letters = Regex("[A-ZÀ-Ö]")
    val numbers = Regex("[0-9]")
    val special = Regex("[^0-9A-ZÀ-Ö]")

    val result = LinkedHashMap<String, Int>()

    special.find(dataString)?.let {
        result["*"] = it.range.first
    }
    numbers.find(dataString)?.let {
        result["#"] = it.range.first
    }
    for (letter in dataString.split("")) {
        if (letters.matches(letter)) {
            result[letter] = dataString.indexOf(letter)
        }
    }
    return result
}

private fun getSelectedIndex(yPosition: Float, sectionIndexHeight: Float, itemCount: Int): Int {
    return (yPosition / sectionIndexHeight * itemCount).toInt().coerceIn(0, itemCount - 1)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SectionIndex(
    data: List<String>,
    listState: LazyListState,
    selectedColour: Color = MaterialTheme.colorScheme.primary,
    minSectionHeight: Dp = 40.dp, // Minimum pixels needed to display each section
) {
    val indexData = getIndexData(data).toList()
    var selectedKey by remember { mutableStateOf("") }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var columnHeightPx by remember { mutableFloatStateOf(0f) }

    val selectIndex = {
        val listIndex = getSelectedIndex(offsetY, columnHeightPx, indexData.size)
        if (selectedKey != indexData[listIndex].first) {
            selectedKey = indexData[listIndex].first
            val index = indexData[listIndex].second
            CoroutineScope(Dispatchers.Main).launch {
                listState.scrollToItem(index)
            }
        }
    }

    val draggableState = remember {
        DraggableState { delta ->
            offsetY += delta
            selectIndex()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(IntrinsicSize.Max)
            .padding(vertical = 10.dp)
            .onGloballyPositioned { coordinates ->
                columnHeightPx = coordinates.size.height.toFloat()
            }
            .draggable(
                orientation = Orientation.Vertical,
                state = draggableState,
                onDragStopped = {
                    selectedKey = ""
                    offsetY = it
                }
            )
            .pointerInteropFilter { motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        offsetY = motionEvent.y
                        selectIndex()
                    }

                    MotionEvent.ACTION_UP -> {
                        offsetY = motionEvent.y
                        selectIndex()
                        selectedKey = ""
                    }
                }
                true
            }
            .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()),
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        with(LocalDensity.current) {
            val minSectionHeightPx = minSectionHeight.toPx()
            val sectionsToShow = (columnHeightPx / minSectionHeightPx).toInt().coerceAtLeast(1)
            val skip = (indexData.size / sectionsToShow).coerceAtLeast(1)
            indexData.filterIndexed { index, _ -> index % skip == 0 }.map {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = it.first,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        color = if (selectedKey == it.first) {
                            selectedColour
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionIndexPreview() {
    SectionIndex(
        "abcdefghijklmnopqrstuvwxyz!@#$%^&*()123456789".split("").filter { it.isNotBlank() },
        LazyListState(),
    )
}