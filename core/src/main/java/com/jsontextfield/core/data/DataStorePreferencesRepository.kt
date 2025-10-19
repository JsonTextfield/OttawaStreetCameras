package com.jsontextfield.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.jsontextfield.core.ui.ThemeMode
import com.jsontextfield.core.ui.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePreferencesRepository(private val dataStore: DataStore<Preferences>) :
    IPreferencesRepository {
    override suspend fun setFavouriteCameras(ids: Set<String>) {
        dataStore.edit { preferences ->
            val key = stringSetPreferencesKey(FAVOURITES_KEY)
            preferences[key] = ids
        }
    }

    override fun getFavourites(): Flow<Set<String>> {
        val key = stringSetPreferencesKey(FAVOURITES_KEY)
        return dataStore.data.map { preferences ->
            preferences[key] ?: emptySet()
        }
    }

    override suspend fun setHiddenCameras(ids: Set<String>) {
        dataStore.edit { preferences ->
            val key = stringSetPreferencesKey(HIDDEN_KEY)
            preferences[key] = ids
        }
    }

    override fun getHidden(): Flow<Set<String>> {
        val key = stringSetPreferencesKey(HIDDEN_KEY)
        return dataStore.data.map { preferences ->
            preferences[key] ?: emptySet()
        }
    }

    override suspend fun setTheme(theme: ThemeMode) {
        dataStore.edit { preferences ->
            val key = intPreferencesKey(THEME_KEY)
            preferences[key] = theme.ordinal
        }
    }

    override fun getTheme(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            ThemeMode.entries.firstOrNull {
                preferences[intPreferencesKey(THEME_KEY)] == it.ordinal
            } ?: ThemeMode.SYSTEM
        }
    }

    override suspend fun setViewMode(viewMode: ViewMode) {
        dataStore.edit { preferences ->
            val key = intPreferencesKey(VIEW_MODE_KEY)
            preferences[key] = viewMode.ordinal
        }
    }

    override fun getViewMode(): Flow<ViewMode> {
        return dataStore.data.map { preferences ->
            ViewMode.entries.firstOrNull {
                preferences[intPreferencesKey(VIEW_MODE_KEY)] == it.ordinal
            } ?: ViewMode.GALLERY
        }
    }

    companion object {
        private const val THEME_KEY = "theme"
        private const val FAVOURITES_KEY = "favourites"
        private const val HIDDEN_KEY = "hidden"
        private const val VIEW_MODE_KEY = "viewMode"
    }
}