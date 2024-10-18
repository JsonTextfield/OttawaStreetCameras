package com.textfield.json.ottawastreetcameras.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesDataStorePreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    IPreferencesRepository {
    override suspend fun favourite(id: String, value: Boolean) {
        val key = booleanPreferencesKey("$id.isFavourite")
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }


    override suspend fun isFavourite(id: String): Boolean {
        val key = booleanPreferencesKey("$id.isFavourite")
        return dataStore.data.map { preferences ->
            preferences[key] ?: false
        }.first()
    }

    override suspend fun setVisibility(id: String, value: Boolean) {
        val key = booleanPreferencesKey("$id.isVisible")
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun isVisible(id: String): Boolean {
        val key = booleanPreferencesKey("$id.isVisible")
        return dataStore.data.map { preferences ->
            preferences[key] ?: true
        }.first()
    }

    override suspend fun setTheme(theme: ThemeMode) {
        val key = intPreferencesKey("theme")
        dataStore.edit { preferences ->
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
        val key = intPreferencesKey("viewMode")
        dataStore.edit { preferences ->
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