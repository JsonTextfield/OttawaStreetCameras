package com.textfield.json.ottawastreetcameras.data

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import kotlinx.coroutines.flow.Flow

interface IPreferencesRepository {
    suspend fun favourite(ids: Collection<String>, value: Boolean)

    fun getFavourites(): Flow<Set<String>>

    suspend fun setVisibility(ids: Collection<String>, value: Boolean)

    fun getHidden(): Flow<Set<String>>

    suspend fun setTheme(theme: ThemeMode)

    fun getTheme(): Flow<ThemeMode>

    suspend fun setViewMode(viewMode: ViewMode)

    fun getViewMode(): Flow<ViewMode>
}

val viewModeKey = intPreferencesKey("viewMode")
val sortModeKey = intPreferencesKey("sortMode")
val themeKey = intPreferencesKey("theme")
val favouritesKey = stringSetPreferencesKey("favourites")
val hiddenKey = stringSetPreferencesKey("hidden")
