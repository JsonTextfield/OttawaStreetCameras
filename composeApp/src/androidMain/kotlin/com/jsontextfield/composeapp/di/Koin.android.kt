package com.jsontextfield.composeapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("prefs")
actual fun dataModule(): Module {
    return module {
        single<DataStore<Preferences>> {
            androidContext().dataStore
        }
    }
}