package com.textfield.json.ottawastreetcameras.di

import android.content.Context
import android.content.SharedPreferences
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.StreetCamsApp
import com.textfield.json.ottawastreetcameras.data.CameraRepository
import com.textfield.json.ottawastreetcameras.data.ICameraRepository
import com.textfield.json.ottawastreetcameras.data.LocalCameraDataSource
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
        return MainViewModel(provideCameraRepository(context), prefs = provideSharedPreferences(context))
    }
}