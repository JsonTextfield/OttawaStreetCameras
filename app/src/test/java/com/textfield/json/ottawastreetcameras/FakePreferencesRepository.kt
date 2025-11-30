package com.textfield.json.ottawastreetcameras

import com.textfield.json.ottawastreetcameras.data.IPreferencesRepository
import com.textfield.json.ottawastreetcameras.ui.main.SortMode
import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf


class FakePreferences : IPreferencesRepository {
    private val favourites = MutableStateFlow(emptySet<String>())
    private val hidden = MutableStateFlow(emptySet<String>())
    private val viewMode = MutableStateFlow(ViewMode.GALLERY)
    private val sortMode = MutableStateFlow(SortMode.NAME)
    private val theme = MutableStateFlow(ThemeMode.SYSTEM)

    override suspend fun favourite(ids: Collection<String>, value: Boolean) {
        val currentFavourites = favourites.value
        val newHidden = if (value) {
            currentFavourites + ids
        }
        else {
            currentFavourites - ids.toSet()
        }
        favourites.value = newHidden
    }

    override fun getFavourites(): Flow<Set<String>> {
        return favourites
    }

    override suspend fun setVisibility(ids: Collection<String>, value: Boolean) {
        val currentHidden = hidden.value
        val newHidden = if (!value) {
            currentHidden + ids
        }
        else {
            currentHidden - ids.toSet()
        }
        hidden.value = newHidden
    }

    override fun getHidden(): Flow<Set<String>> {
        return hidden
    }

    override suspend fun setTheme(theme: ThemeMode) {
        this.theme.value = theme
    }

    override fun getTheme(): Flow<ThemeMode> {
        return theme
    }

    override suspend fun setViewMode(viewMode: ViewMode) {
        this.viewMode.value = viewMode
    }

    override fun getViewMode(): Flow<ViewMode> {
        return viewMode
    }

}