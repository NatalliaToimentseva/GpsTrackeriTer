package com.iTergt.routgpstracker.ui.routes.domain

import androidx.paging.PagingData
import androidx.paging.map
import com.iTergt.routgpstracker.models.RouteModel
import com.iTergt.routgpstracker.repository.RouteRepository
import io.reactivex.rxjava3.core.Flowable

class LoadRoutesUseCase(private val repository: RouteRepository) {

    fun loadRoutes(): Flowable<PagingData<RouteModel>> {
        return repository.getRoutes().map { value ->
            value.map { routeEntity ->
                routeEntity.toRouteModel()
            }
        }
    }
}