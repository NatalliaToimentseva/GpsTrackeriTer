package com.iTergt.routgpstracker.di

import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

val fireBaseAuthModule = module {
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
}