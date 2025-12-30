package com.jsontextfield.core.data

import com.jsontextfield.core.entities.City
import com.jsontextfield.core.ui.ThemeMode
import com.jsontextfield.core.ui.ViewMode
import kotlinx.coroutines.flow.Flow

interface IPreferencesRepository {
    suspend fun setFavouriteCameras(ids: Set<String>)

    fun getFavourites(): Flow<Set<String>>

    suspend fun setHiddenCameras(ids: Set<String>)

    fun getHidden(): Flow<Set<String>>

    suspend fun setTheme(theme: ThemeMode)

    fun getTheme(): Flow<ThemeMode>

    suspend fun setViewMode(viewMode: ViewMode)

    fun getViewMode(): Flow<ViewMode>

    fun getCity(): Flow<City>

    suspend fun setCity(city: City)
}