package com.textfield.json.ottawastreetcameras.ui.components

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.textfield.json.ottawastreetcameras.comparators.SortByName
import com.textfield.json.ottawastreetcameras.entities.Camera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private fun getIndexHashMap(cameras: List<Camera>): LinkedHashMap<String, Int> {
    val dataString = cameras.sortedWith(SortByName()).map {
        it.sortableName.first()
    }.joinToString("")

    val letters = Regex("[A-ZÀ-Ö]")
    val numbers = Regex("[0-9]")
    val special = Regex("[^0-9A-ZÀ-Ö]")

    val result = LinkedHashMap<String, Int>()
    if (special.matches(dataString)) {
        result["*"] = special.find(dataString)?.range?.first!!
    }
    if (numbers.matches(dataString)) {
        result["#"] = numbers.find(dataString)?.range?.first!!
    }
    for (character in dataString.split("")) {
        if (letters.matches(character)) {
            result[character] = dataString.indexOf(character)
        }
    }
    Log.d("SectionIndex", dataString)
    Log.d("SectionIndex", result.toString())
    return result
}

private fun getIndex(yPosition: Float, positions: ArrayList<Int>, sectionIndexHeight: Int): Int {
    return positions[(yPosition / sectionIndexHeight * positions.size).toInt().coerceIn(0, positions.size - 1)]
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SectionIndex(cameras: List<Camera>, listState: LazyListState) {
    val result = getIndexHashMap(cameras)
    val height = LocalConfiguration.current.screenHeightDp
    var offsetY by remember { mutableStateOf(0f) }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 10.dp)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    offsetY += delta
                    CoroutineScope(Dispatchers.Main).launch {
                        listState.scrollToItem(getIndex(offsetY, ArrayList(result.values), height))
                    }
                }
            )
            .pointerInteropFilter { motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            listState.scrollToItem(getIndex(motionEvent.rawY, ArrayList(result.values), height))
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {}
                    MotionEvent.ACTION_UP -> {}
                    else -> false
                }
                true
            },
    ) {
        result.entries.map {
            Text(
                text = it.key,
                fontSize = 10.sp,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 10.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}