package com.textfield.json.ottawastreetcameras

import android.app.Application
import android.content.res.Resources
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StreetCamsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        _resources = resources
    }

    companion object {
        private var _resources: Resources? = null
        val resources: Resources get() = _resources!!
    }
}