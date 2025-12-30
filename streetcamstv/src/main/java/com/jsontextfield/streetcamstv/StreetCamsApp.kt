package com.jsontextfield.streetcamstv

import android.app.Application
import com.jsontextfield.streetcamstv.di.initKoin

class StreetCamsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this@StreetCamsApp)
    }
}