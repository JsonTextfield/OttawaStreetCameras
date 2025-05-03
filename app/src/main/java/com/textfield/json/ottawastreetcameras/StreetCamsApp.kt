package com.textfield.json.ottawastreetcameras

import android.app.Application
import com.textfield.json.ottawastreetcameras.di.initKoin

class StreetCamsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this@StreetCamsApp)
    }
}