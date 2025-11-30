package com.textfield.json.ottawastreetcameras.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStorePreferencesRepository(private val dataStore: DataStore<Preferences>) :
    IPreferencesRepository {
    override suspend fun favourite(ids: Collection<String>, value: Boolean) {
        dataStore.edit { preferences ->
            val currentFavourites = getFavourites().first()
            val newFavourites = if (value) {
                currentFavourites + ids
            }
            else {
                currentFavourites - ids.toSet()
            }
            preferences[favouritesKey] = newFavourites
        }
    }

    override fun getFavourites(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[favouritesKey] ?: emptySet()
        }
    }

    override suspend fun setVisibility(ids: Collection<String>, value: Boolean) {
        dataStore.edit { preferences ->
            val currentHidden = getHidden().first()
            val newHidden = if (!value) {
                currentHidden + ids
            }
            else {
                currentHidden - ids.toSet()
            }
            preferences[hiddenKey] = newHidden
        }
    }

    override fun getHidden(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[hiddenKey] ?: emptySet()
        }
    }

    override suspend fun setTheme(theme: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[themeKey] = theme.ordinal
        }
    }

    override fun getTheme(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            ThemeMode.entries.firstOrNull { preferences[themeKey] == it.ordinal } ?: ThemeMode.SYSTEM
        }
    }

    override suspend fun setViewMode(viewMode: ViewMode) {
        dataStore.edit { preferences ->
            preferences[viewModeKey] = viewMode.ordinal
        }
    }

    override fun getViewMode(): Flow<ViewMode> {
        return dataStore.data.map { preferences ->
            ViewMode.entries.firstOrNull { preferences[viewModeKey] == it.ordinal } ?: ViewMode.GALLERY
        }
    }
}