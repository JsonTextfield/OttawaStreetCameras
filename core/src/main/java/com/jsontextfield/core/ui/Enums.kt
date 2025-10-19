package com.jsontextfield.core.ui

import com.jsontextfield.core.R

enum class ThemeMode(val key: Int) {
    LIGHT(R.string.light_mode),
    DARK(R.string.dark_mode),
    SYSTEM(R.string.system_default),
}

enum class SortMode(val key: Int) {
    NAME(R.string.sort_by_name),
    DISTANCE(R.string.sort_by_distance),
    NEIGHBOURHOOD(R.string.sort_by_neighbourhood),
}

enum class FilterMode(val key: Int) {
    VISIBLE(R.string.app_name),
    FAVOURITE(R.string.favourites),
    HIDDEN(R.string.hidden_cameras),
}

enum class SearchMode { NONE, NAME, NEIGHBOURHOOD, }

enum class ViewMode(val key: Int) {
    LIST(R.string.list),
    MAP(R.string.map),
    GALLERY(R.string.gallery),
}

enum class Status { LOADING, LOADED, ERROR, INITIAL, }