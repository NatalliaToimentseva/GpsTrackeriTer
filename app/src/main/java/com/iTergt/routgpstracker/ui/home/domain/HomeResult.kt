package com.iTergt.routgpstracker.ui.home.domain

sealed class HomeResult {

    data object Success : HomeResult()

    data class Error(val message: String?) : HomeResult()
}