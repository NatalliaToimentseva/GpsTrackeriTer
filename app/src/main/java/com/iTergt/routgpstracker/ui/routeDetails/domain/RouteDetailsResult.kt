package com.iTergt.routgpstracker.ui.routeDetails.domain

import com.iTergt.routgpstracker.models.RouteModel

sealed class RouteDetailsResult {

    data class Success(val route: RouteModel) : RouteDetailsResult()

    data class Error(val message: String?) : RouteDetailsResult()
}