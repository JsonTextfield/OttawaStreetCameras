package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode

interface IPreferencesRepository {
    suspend fun favourite(id: String, value: Boolean)

    suspend fun isFavourite(id: String): Boolean

    suspend fun setVisibility(id: String, value: Boolean)

    suspend fun isVisible(id: String): Boolean

    suspend fun setTheme(theme: ThemeMode)

    suspend fun getTheme(): ThemeMode

    suspend fun setViewMode(viewMode: ViewMode)

    suspend fun getViewMode(): ViewMode
}