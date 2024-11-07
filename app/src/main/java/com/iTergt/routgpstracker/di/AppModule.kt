package com.iTergt.routgpstracker.di

import com.iTergt.routgpstracker.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    viewModelOf(::HomeViewModel)
}