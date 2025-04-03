package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.data.IPreferencesRepository
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode


class FakePreferences : IPreferencesRepository {
    private val data = mutableMapOf<String, Any>()

    override suspend fun favourite(ids: Collection<String>, value: Boolean) {
        val currentFavourites = data["favourites"] as? Set<String> ?: emptySet()
        val newHidden = if (value) {
            currentFavourites + ids
        }
        else {
            currentFavourites - ids.toSet()
        }
        data["favourites"] = newHidden
    }

    override suspend fun getFavourites(): List<String> {
        return (data["favourites"] as? Set<String> ?: emptySet()).toList()
    }

    override suspend fun setVisibility(ids: Collection<String>, value: Boolean) {
        val currentHidden = data["hidden"] as? Set<String> ?: emptySet()
        val newHidden = if (!value) {
            currentHidden + ids
        }
        else {
            currentHidden - ids.toSet()
        }
        data["hidden"] = newHidden
    }

    override suspend fun getHidden(): List<String> {
        return (data["hidden"] as? Set<String> ?: emptySet()).toList()
    }

    override suspend fun setTheme(theme: ThemeMode) {
        data["theme"] = theme
    }

    override suspend fun getTheme(): ThemeMode {
        return (data["theme"] ?: ThemeMode.SYSTEM) as ThemeMode
    }

    override suspend fun setViewMode(viewMode: ViewMode) {
        data["viewMode"] = viewMode
    }

    override suspend fun getViewMode(): ViewMode {
        return (data["viewMode"] ?: ViewMode.GALLERY) as ViewMode
    }

}