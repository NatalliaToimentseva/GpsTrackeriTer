package com.iTergt.routgpstracker.di

import com.iTergt.routgpstracker.repository.RouteRepository
import com.iTergt.routgpstracker.repository.RouteRepositoryImpl
import com.iTergt.routgpstracker.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    factory<RouteRepository> { RouteRepositoryImpl(get())}

    viewModelOf(::HomeViewModel)
}