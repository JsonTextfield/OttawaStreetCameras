package com.textfield.json.ottawastreetcameras.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesDataStorePreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    IPreferencesRepository {

    override suspend fun favourite(ids: Collection<String>, value: Boolean) {
        dataStore.edit { preferences ->
            val currentFavourites = getFavourites().toMutableSet()
            val newFavourites = if (value) {
                currentFavourites + ids
            }
            else {
                currentFavourites - ids.toSet()
            }
            val key = stringSetPreferencesKey("favourites")
            preferences[key] = newFavourites
        }
    }

    override suspend fun getFavourites(): List<String> {
        val key = stringSetPreferencesKey("favourites")
        return dataStore.data.map { preferences ->
            preferences[key] ?: emptySet()
        }.first().toList()
    }

    override suspend fun setVisibility(ids: Collection<String>, value: Boolean) {
        dataStore.edit { preferences ->
            val currentHidden = getHidden().toMutableSet()
            val newHidden = if (!value) {
                currentHidden + ids
            }
            else {
                currentHidden - ids.toSet()
            }
            val key = stringSetPreferencesKey("hidden")
            preferences[key] = newHidden
        }
    }

    override suspend fun getHidden(): List<String> {
        val key = stringSetPreferencesKey("hidden")
        return dataStore.data.map { preferences ->
            preferences[key] ?: emptySet()
        }.first().toList()
    }

    override suspend fun setTheme(theme: ThemeMode) {
        dataStore.edit { preferences ->
            val key = intPreferencesKey("theme")
            preferences[key] = theme.ordinal
        }
    }

    override suspend fun getTheme(): ThemeMode {
        val key = intPreferencesKey("theme")
        return dataStore.data.map { preferences ->
            ThemeMode.entries[preferences[key] ?: ThemeMode.SYSTEM.ordinal]
        }.first()
    }

    override suspend fun setViewMode(viewMode: ViewMode) {
        dataStore.edit { preferences ->
            val key = intPreferencesKey("viewMode")
            preferences[key] = viewMode.ordinal
        }
    }

    override suspend fun getViewMode(): ViewMode {
        val key = intPreferencesKey("viewMode")
        return dataStore.data.map { preferences ->
            ViewMode.entries[preferences[key] ?: ViewMode.LIST.ordinal]
        }.first()
    }
}