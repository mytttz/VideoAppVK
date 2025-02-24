package com.example.videoappvk

import android.app.Application
import com.example.videoappvk.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VideoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@VideoApp)
            modules(appModule)
        }
    }
}