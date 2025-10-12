package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.data.IPreferencesRepository
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class FakePreferences : IPreferencesRepository {
    private val data = mutableMapOf<String, Any>()

    override suspend fun setFavouriteCameras(ids: Set<String>) {
        data["favourites"] = ids
    }

    override fun getFavourites(): Flow<Set<String>> {
        return flowOf(data["favourites"] as? Set<String> ?: emptySet())
    }

    override suspend fun setHiddenCameras(ids: Set<String>) {
        data["hidden"] = ids
    }

    override fun getHidden(): Flow<Set<String>> {
        return flowOf(data["hidden"] as? Set<String> ?: emptySet())
    }

    override suspend fun setTheme(theme: ThemeMode) {
        data["theme"] = theme
    }

    override fun getTheme(): Flow<ThemeMode> {
        return flowOf((data["theme"] ?: ThemeMode.SYSTEM) as ThemeMode)
    }

    override suspend fun setViewMode(viewMode: ViewMode) {
        data["viewMode"] = viewMode
    }

    override fun getViewMode(): Flow<ViewMode> {
        return flowOf((data["viewMode"] ?: ViewMode.GALLERY) as ViewMode)
    }

}