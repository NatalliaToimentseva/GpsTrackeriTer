package com.iTergt.routgpstracker.di

import androidx.room.Room
import com.iTergt.routgpstracker.dataBase.AppDataBase
import com.iTergt.routgpstracker.dataBase.dao.RouteDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val DATA_BASE_NAME = "app_data_base"

val dataBaseModule = module {

    single<RouteDao> {
        val appDataBase: AppDataBase =
            Room.databaseBuilder(androidContext(), AppDataBase::class.java, DATA_BASE_NAME)
                .build()
        appDataBase.getRouteDao()
    }
}