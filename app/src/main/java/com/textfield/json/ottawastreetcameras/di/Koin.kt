package com.textfield.json.ottawastreetcameras.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.jsontextfield.core.data.CameraRepository
import com.jsontextfield.core.data.DataStorePreferencesRepository
import com.jsontextfield.core.data.ICameraRepository
import com.jsontextfield.core.data.IPreferencesRepository
import com.jsontextfield.core.network.SUPABASE_API_KEY
import com.jsontextfield.core.ui.main.CameraState
import com.jsontextfield.core.ui.viewmodels.CameraViewModel
import com.jsontextfield.core.ui.viewmodels.MainViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
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
            supabaseKey = SUPABASE_API_KEY
        ) {
            install(Postgrest)
        }
    }

    single<ICameraRepository> { CameraRepository(get<SupabaseClient>()) }

    single { MutableStateFlow(CameraState()) }

    viewModelOf(::MainViewModel)
    viewModel<CameraViewModel> { parameters ->
        CameraViewModel(
            cameraRepository = get<ICameraRepository>(),
            cameraIds = parameters.getOrNull(String::class) ?: "",
            isShuffling = parameters.getOrNull(Boolean::class) ?: true,
        )
    }
}

fun initKoin(context: Context) {
    startKoin {
        androidContext(context)
        modules(appModule)
    }
}