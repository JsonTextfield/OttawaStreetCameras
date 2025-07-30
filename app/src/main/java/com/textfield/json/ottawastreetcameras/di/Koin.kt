package com.textfield.json.ottawastreetcameras.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.textfield.json.ottawastreetcameras.R
import com.textfield.json.ottawastreetcameras.data.CameraRepository
import com.textfield.json.ottawastreetcameras.data.DataStorePreferencesRepository
import com.textfield.json.ottawastreetcameras.data.ICameraDataSource
import com.textfield.json.ottawastreetcameras.data.ICameraRepository
import com.textfield.json.ottawastreetcameras.data.IPreferencesRepository
import com.textfield.json.ottawastreetcameras.data.SupabaseCameraDataSource
import com.textfield.json.ottawastreetcameras.ui.camera.CameraViewModel
import com.textfield.json.ottawastreetcameras.ui.main.CameraState
import com.textfield.json.ottawastreetcameras.ui.main.MainViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("prefs")

val appModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }

    single<DataStore<Preferences>> {
        androidContext().dataStore
    }

    single<IPreferencesRepository> {
        DataStorePreferencesRepository(get<DataStore<Preferences>>())
    }

    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = "https://nacudfxzbqaesoyjfluh.supabase.co",
            supabaseKey = androidContext().getString(R.string.supabase_key)
        ) {
            install(Postgrest)
        }
    }

    single<ICameraDataSource> { SupabaseCameraDataSource(get<SupabaseClient>()) }

    single<ICameraRepository> { CameraRepository(get<ICameraDataSource>()) }

    single { MutableStateFlow(CameraState()) }

    factoryOf(::MainViewModel)
    factoryOf(::CameraViewModel)
}

fun initKoin(context: Context) {
    startKoin {
        androidContext(context)
        modules(appModule)
    }
}