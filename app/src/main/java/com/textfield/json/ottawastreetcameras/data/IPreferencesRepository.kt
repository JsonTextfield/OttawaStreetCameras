package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode

interface IPreferencesRepository {
    suspend fun favourite(ids: Collection<String>, value: Boolean)

    suspend fun getFavourites(): List<String>

    suspend fun setVisibility(ids: Collection<String>, value: Boolean)

    suspend fun getHidden(): List<String>

    suspend fun setTheme(theme: ThemeMode)

    suspend fun getTheme(): ThemeMode

    suspend fun setViewMode(viewMode: ViewMode)

    suspend fun getViewMode(): ViewMode
}