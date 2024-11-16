package com.iTergt.routgpstracker.di

import com.iTergt.routgpstracker.repository.RouteRepository
import com.iTergt.routgpstracker.repository.RouteRepositoryImpl
import com.iTergt.routgpstracker.ui.home.HomeViewModel
import com.iTergt.routgpstracker.ui.home.domain.SaveRouteUseCase
import com.iTergt.routgpstracker.ui.routeDetails.RouteDetailsViewModel
import com.iTergt.routgpstracker.ui.routeDetails.domain.LoadRouteUseCase
import com.iTergt.routgpstracker.ui.routes.RoutesViewModel
import com.iTergt.routgpstracker.ui.routes.domain.DeleteRouteUseCase
import com.iTergt.routgpstracker.ui.routes.domain.LoadRoutesUseCase
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    factory<RouteRepository> { RouteRepositoryImpl(get()) }

    factory<SaveRouteUseCase> { SaveRouteUseCase(get()) }

    factory<LoadRoutesUseCase> { LoadRoutesUseCase(get()) }

    factory<LoadRouteUseCase> { LoadRouteUseCase(get()) }

    factory<DeleteRouteUseCase> { DeleteRouteUseCase(get()) }

    viewModelOf(::HomeViewModel)

    viewModelOf(::RoutesViewModel)

    viewModelOf(::RouteDetailsViewModel)
}