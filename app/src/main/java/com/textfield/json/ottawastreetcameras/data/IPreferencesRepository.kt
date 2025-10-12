package com.textfield.json.ottawastreetcameras.data

import com.textfield.json.ottawastreetcameras.ui.main.ThemeMode
import com.textfield.json.ottawastreetcameras.ui.main.ViewMode
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
}