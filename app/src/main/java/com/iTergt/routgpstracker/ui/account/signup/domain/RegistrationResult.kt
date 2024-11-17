package com.iTergt.routgpstracker.ui.account.signup.domain

sealed class RegistrationResult {

    data class Success(val uid: String) : RegistrationResult()

    data class Error(val message: String) : RegistrationResult()
}