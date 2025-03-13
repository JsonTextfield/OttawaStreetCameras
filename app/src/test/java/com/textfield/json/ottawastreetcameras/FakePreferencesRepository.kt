package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.data.IPreferencesRepository
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode


class FakePreferences : IPreferencesRepository {
    private val data = mutableMapOf<String, Any>()
    override suspend fun favourite(id: String, value: Boolean) {
        data[id] = value
    }

    override suspend fun isFavourite(id: String): Boolean {
        return (data[id] ?: false) as Boolean
    }

    override suspend fun setVisibility(id: String, value: Boolean) {
        data[id] = value
    }

    override suspend fun isVisible(id: String): Boolean {
        return (data[id] ?: true) as Boolean
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