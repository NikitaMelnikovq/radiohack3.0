package com.radiohack.tloyalty

import android.app.Application

class TloyaltyApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
