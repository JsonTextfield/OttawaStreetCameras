package com.textfield.json.ottawastreetcameras.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(private val sharedPreferences: SharedPreferences) :
    IPreferencesRepository {
    override suspend fun favourite(ids: Collection<String>, value: Boolean) {
        sharedPreferences.edit {
            val favourites = getFavourites().toMutableSet()
            putStringSet("favourites", if (value) favourites + ids else favourites - ids.toSet())
        }
    }

    override suspend fun getFavourites(): List<String> {
        return sharedPreferences.getStringSet("favourites", emptySet())?.toList() ?: emptyList()
    }

    override suspend fun setVisibility(ids: Collection<String>, value: Boolean) {
        sharedPreferences.edit {
            val hidden = getHidden().toMutableSet()
            putStringSet("hidden", if (value) hidden + ids else hidden - ids.toSet())
        }
    }

    override suspend fun getHidden(): List<String> {
        return sharedPreferences.getStringSet("hidden", emptySet())?.toList() ?: emptyList()
    }

    override suspend fun setTheme(theme: ThemeMode) {
        sharedPreferences.edit {
            putInt("theme", theme.ordinal)
        }
    }

    override suspend fun getTheme(): ThemeMode {
        return ThemeMode.entries[
            sharedPreferences.getInt("theme", ThemeMode.SYSTEM.ordinal)
        ]
    }

    override suspend fun setViewMode(viewMode: ViewMode) {
        sharedPreferences.edit {
            putInt("viewMode", viewMode.ordinal)
        }
    }

    override suspend fun getViewMode(): ViewMode {
        return ViewMode.entries[
            sharedPreferences.getInt("viewMode", ViewMode.LIST.ordinal)
        ]
    }
}