package com.iTergt.routgpstracker.ui.routes

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.iTergt.routgpstracker.models.RouteModel
import com.iTergt.routgpstracker.ui.routes.domain.DeleteRouteUseCase
import com.iTergt.routgpstracker.ui.routes.domain.LoadRoutesUseCase
import com.iTergt.routgpstracker.ui.routes.domain.RoutesResult
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable

class RoutesViewModel(
    private val loadRoutesUseCase: LoadRoutesUseCase,
    private val deleteRouteUseCase: DeleteRouteUseCase
) : ViewModel() {

    var operationResult: ((message: String?) -> Unit)? = null
    private val disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun getListRoutes(): Flowable<PagingData<RouteModel>> = loadRoutesUseCase.loadRoutes()

    fun deleteRoute(routeModel: RouteModel) {
        disposable.add(
            deleteRouteUseCase.deleteRoute(routeModel)
                .subscribe(
                    { result ->
                        handleResult(result)
                    },
                    { error ->
                        error.message?.let {
                            handleResult(RoutesResult.Error(it))
                        }
                    }
                )
        )
    }

    private fun handleResult(result: RoutesResult) {
        when (result) {
            is RoutesResult.Error -> operationResult?.invoke(result.message)
            is RoutesResult.Success -> operationResult?.invoke(null)
        }
    }
}