package com.iTergt.routgpstracker

import android.app.Application
import com.iTergt.routgpstracker.di.appModule
import com.iTergt.routgpstracker.di.serviceModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(serviceModule, appModule)
        }
    }
}