package com.textfield.json.ottawastreetcameras.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.data.CameraRepository
import com.textfield.json.ottawastreetcameras.data.ICameraDataSource
import com.textfield.json.ottawastreetcameras.data.ICameraRepository
import com.textfield.json.ottawastreetcameras.data.IPreferencesRepository
import com.textfield.json.ottawastreetcameras.data.PreferencesDataStorePreferencesRepository
import com.textfield.json.ottawastreetcameras.data.SupabaseCameraDataSource
import com.textfield.json.ottawastreetcameras.ui.main.CameraState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideSupabaseClient(@ApplicationContext context: Context): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://nacudfxzbqaesoyjfluh.supabase.co",
            supabaseKey = context.getString(R.string.supabase_key)
        ) {
            install(Postgrest)
        }
    }

    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("prefs")

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Singleton
    @Provides
    fun providePreferencesRepository(
        //sharedPreferences: SharedPreferences,
        dataStore: DataStore<Preferences>,
    ): IPreferencesRepository {
        return PreferencesDataStorePreferencesRepository(dataStore)
        //return SharedPreferencesRepository(sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideCameraDataSource(supabaseClient: SupabaseClient): ICameraDataSource {
        return SupabaseCameraDataSource(supabaseClient)
    }

    @Singleton
    @Provides
    fun provideCameraRepository(dataSource: ICameraDataSource): ICameraRepository {
        return CameraRepository(dataSource)
    }

    @Singleton
    @Provides
    fun provideCameraState() : MutableStateFlow<CameraState> {
        return MutableStateFlow(CameraState())
    }
}