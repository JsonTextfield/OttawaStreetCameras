package com.jsontextfield.composeapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.jsontextfield.shared.data.CameraRepository
import com.jsontextfield.shared.data.DataStorePreferencesRepository
import com.jsontextfield.shared.data.ICameraRepository
import com.jsontextfield.shared.data.IPreferencesRepository
import com.jsontextfield.shared.network.SUPABASE_API_KEY
import com.jsontextfield.shared.ui.main.CameraState
import com.jsontextfield.shared.ui.viewmodels.CameraViewModel
import com.jsontextfield.shared.ui.viewmodels.MainViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect fun dataModule(): Module

val appModule = module {

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
            prefRepository = get<IPreferencesRepository>(),
        )
    }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            appModule,
            dataModule(),
        )
    }
}