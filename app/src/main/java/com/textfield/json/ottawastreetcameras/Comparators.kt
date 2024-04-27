package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.entities.Camera

val SortByName = compareBy<Camera> { it.sortableName }
val SortByDistance = compareBy<Camera>({ it.distance }, { it.sortableName })
val SortByNeighbourhood = compareBy<Camera>({ it.neighbourhood }, { it.sortableName })