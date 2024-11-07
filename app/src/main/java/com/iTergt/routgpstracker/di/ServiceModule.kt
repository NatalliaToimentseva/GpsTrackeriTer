package com.iTergt.routgpstracker.di

import com.iTergt.routgpstracker.controllers.LocationController
import com.iTergt.routgpstracker.service.NotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val serviceModule = module {

    single<NotificationManager> {
        NotificationManager(androidContext())
    }

    single<LocationController> {
        LocationController()
    }
}