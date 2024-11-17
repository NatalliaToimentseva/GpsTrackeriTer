package com.iTergt.routgpstracker.di

import com.iTergt.routgpstracker.repository.RouteRepository
import com.iTergt.routgpstracker.repository.RouteRepositoryImpl
import com.iTergt.routgpstracker.repository.SharedPreferencesRepository
import com.iTergt.routgpstracker.ui.account.login.LoginViewModel
import com.iTergt.routgpstracker.ui.account.login.domain.LoginUseCase
import com.iTergt.routgpstracker.ui.account.signup.SignupViewModel
import com.iTergt.routgpstracker.ui.account.signup.domain.RegisterAccountUseCase
import com.iTergt.routgpstracker.ui.tabs.home.HomeViewModel
import com.iTergt.routgpstracker.ui.tabs.home.domain.SaveRouteUseCase
import com.iTergt.routgpstracker.ui.routeDetails.RouteDetailsViewModel
import com.iTergt.routgpstracker.ui.routeDetails.domain.LoadRouteUseCase
import com.iTergt.routgpstracker.ui.tabs.routes.RoutesViewModel
import com.iTergt.routgpstracker.ui.tabs.routes.domain.DeleteRouteUseCase
import com.iTergt.routgpstracker.ui.tabs.routes.domain.LoadRoutesUseCase
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    factory<RouteRepository> { RouteRepositoryImpl(get()) }

    factory<SaveRouteUseCase> { SaveRouteUseCase(get()) }

    factory<LoadRoutesUseCase> { LoadRoutesUseCase(get()) }

    factory<LoadRouteUseCase> { LoadRouteUseCase(get()) }

    factory<DeleteRouteUseCase> { DeleteRouteUseCase(get()) }

    factory<RegisterAccountUseCase> { RegisterAccountUseCase(get()) }

    factory<LoginUseCase> { LoginUseCase(get()) }

    single<SharedPreferencesRepository> { SharedPreferencesRepository(get()) }

    viewModelOf(::HomeViewModel)

    viewModelOf(::RoutesViewModel)

    viewModelOf(::RouteDetailsViewModel)

    viewModelOf(::LoginViewModel)

    viewModelOf(::SignupViewModel)
}