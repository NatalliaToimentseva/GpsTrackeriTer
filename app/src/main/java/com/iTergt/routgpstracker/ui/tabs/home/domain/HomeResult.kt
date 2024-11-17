package com.iTergt.routgpstracker.ui.tabs.home.domain

sealed class HomeResult {

    data object Success : HomeResult()

    data class Error(val message: String?) : HomeResult()
}