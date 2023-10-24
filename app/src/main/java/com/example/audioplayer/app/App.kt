package com.example.audioplayer.app

import android.app.Application
import com.example.audioplayer.app.modules.dataModule
import com.example.audioplayer.app.modules.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)

            modules(dataModule, presentationModule)
        }
    }
}