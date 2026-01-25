package com.jsontextfield.shared.ui

import org.jetbrains.compose.resources.StringResource
import streetcams.shared.generated.resources.Res
import streetcams.shared.generated.resources.app_name
import streetcams.shared.generated.resources.dark_mode
import streetcams.shared.generated.resources.favourites
import streetcams.shared.generated.resources.gallery
import streetcams.shared.generated.resources.hidden_cameras
import streetcams.shared.generated.resources.light_mode
import streetcams.shared.generated.resources.list
import streetcams.shared.generated.resources.map
import streetcams.shared.generated.resources.sort_by_distance
import streetcams.shared.generated.resources.sort_by_name
import streetcams.shared.generated.resources.sort_by_neighbourhood
import streetcams.shared.generated.resources.system_default

enum class ThemeMode(val key: StringResource) {
    LIGHT(Res.string.light_mode),
    DARK(Res.string.dark_mode),
    SYSTEM(Res.string.system_default),
}

enum class SortMode(val key: StringResource) {
    NAME(Res.string.sort_by_name),
    DISTANCE(Res.string.sort_by_distance),
    NEIGHBOURHOOD(Res.string.sort_by_neighbourhood),
}

enum class FilterMode(val key: StringResource) {
    VISIBLE(Res.string.app_name),
    FAVOURITE(Res.string.favourites),
    HIDDEN(Res.string.hidden_cameras),
}

enum class SearchMode { NONE, NAME, NEIGHBOURHOOD, }

enum class ViewMode(val key: StringResource) {
    LIST(Res.string.list),
    MAP(Res.string.map),
    GALLERY(Res.string.gallery),
}

enum class Status { LOADING, LOADED, ERROR, INITIAL, }