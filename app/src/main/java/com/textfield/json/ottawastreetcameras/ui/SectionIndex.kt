package com.textfield.json.ottawastreetcameras.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.textfield.json.ottawastreetcameras.entities.Camera

@Composable
fun SectionIndex(cameras: List<Camera>) {
    val dataString = cameras.map {
        it.getSortableName().first()
    }.sorted().joinToString()

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

    Column(modifier = Modifier.fillMaxHeight().padding(vertical = 10.dp)) {
        result.keys.map {
            Text(
                text = it,
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