package com.jsontextfield.shared

import com.jsontextfield.shared.data.IPreferencesRepository
import com.jsontextfield.shared.entities.City
import com.jsontextfield.shared.ui.ThemeMode
import com.jsontextfield.shared.ui.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class FakePreferences : IPreferencesRepository {
    private val favouritesFlow = MutableStateFlow(emptySet<String>())
    override suspend fun setFavouriteCameras(ids: Set<String>) {
        favouritesFlow.value = ids
    }

    override fun getFavourites(): Flow<Set<String>> = favouritesFlow

    private val hiddenCamerasFlow = MutableStateFlow(emptySet<String>())
    override suspend fun setHiddenCameras(ids: Set<String>) {
        hiddenCamerasFlow.value = ids
    }

    override fun getHidden(): Flow<Set<String>> = hiddenCamerasFlow

    private val themeFlow = MutableStateFlow(ThemeMode.SYSTEM)
    override suspend fun setTheme(theme: ThemeMode) {
        themeFlow.value = theme
    }

    override fun getTheme(): Flow<ThemeMode> = themeFlow

    private val viewModeFlow = MutableStateFlow(ViewMode.GALLERY)
    override suspend fun setViewMode(viewMode: ViewMode) {
        viewModeFlow.value = viewMode
    }

    override fun getViewMode(): Flow<ViewMode> = viewModeFlow

    private val cityFlow = MutableStateFlow(City.OTTAWA)
    override suspend fun setCity(city: City) {
        cityFlow.value = city
    }

    override fun getCity(): Flow<City> = cityFlow
}