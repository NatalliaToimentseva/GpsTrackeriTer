package com.iTergt.routgpstracker.ui.routes.domain

sealed class RoutesResult {

    data object Success : RoutesResult()

    data class Error(val message: String?) : RoutesResult()
}