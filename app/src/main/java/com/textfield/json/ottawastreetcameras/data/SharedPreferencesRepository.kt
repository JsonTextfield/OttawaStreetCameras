package com.textfield.json.ottawastreetcameras.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(private val sharedPreferences: SharedPreferences) : IPreferencesRepository {
    override suspend fun favourite(id: String, value: Boolean) {
        sharedPreferences.edit {
            putBoolean("$id.isFavourite", value)
        }
    }

    override suspend fun isFavourite(id: String): Boolean {
        return sharedPreferences.getBoolean("$id.isFavourite", false)
    }

    override suspend fun setVisibility(id: String, value: Boolean) {
        sharedPreferences.edit {
            putBoolean("$id.isVisible", value)
        }
    }

    override suspend fun isVisible(id: String): Boolean {
        return sharedPreferences.getBoolean("$id.isVisible", true)
    }

    override suspend fun setTheme(theme: ThemeMode) {
        sharedPreferences.edit {
            putInt("theme", theme.ordinal)
        }
    }

    override suspend fun getTheme(): ThemeMode {
        return ThemeMode.entries[sharedPreferences.getInt("theme", ThemeMode.SYSTEM.ordinal)]
    }

    override suspend fun setViewMode(viewMode: ViewMode) {
        sharedPreferences.edit {
            putString("viewMode", viewMode.name)
        }
    }

    override suspend fun getViewMode(): ViewMode {
        return ViewMode.valueOf(sharedPreferences.getString("viewMode", ViewMode.LIST.name) ?: ViewMode.LIST.name)
    }
}