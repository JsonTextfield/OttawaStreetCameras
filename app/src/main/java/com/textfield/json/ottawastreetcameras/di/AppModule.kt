package com.textfield.json.ottawastreetcameras.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsApp
import com.textfield.json.ottawastreetcameras.data.CameraRepository
import com.textfield.json.ottawastreetcameras.data.ICameraRepository
import com.textfield.json.ottawastreetcameras.data.IPreferencesRepository
import com.textfield.json.ottawastreetcameras.data.LocalCameraDataSource
import com.textfield.json.ottawastreetcameras.data.PreferencesDataStorePreferencesRepository
import com.textfield.json.ottawastreetcameras.data.SharedPreferencesRepository
import com.textfield.json.ottawastreetcameras.ui.main.MainViewModel
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @get:Provides
    val supabase: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://nacudfxzbqaesoyjfluh.supabase.co",
            supabaseKey = StreetCamsApp.resources.getString(R.string.supabase_key)
        ) {
            install(Postgrest)
        }
    }

    @get:Provides
    val dispatcher: CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideCameraRepository(@ApplicationContext context: Context): ICameraRepository {
        return CameraRepository(LocalCameraDataSource(context))
    }

    @Provides
    fun provideMainViewModel(@ApplicationContext context: Context): MainViewModel {
        return MainViewModel(
            cameraRepository = provideCameraRepository(context),
            prefs = providePreferencesDataStoreRepository(context),
            dispatcher = dispatcher
        )
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesRepository(@ApplicationContext context: Context): IPreferencesRepository {
        return SharedPreferencesRepository(provideSharedPreferences(context))
    }

    @Singleton
    @Provides
    fun providePreferencesDataStoreRepository(@ApplicationContext context: Context): IPreferencesRepository {
        return PreferencesDataStorePreferencesRepository(context.dataStore)
    }

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return preferencesDataStore(context.packageName).getValue(context, String::javaClass)
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("prefs")
}