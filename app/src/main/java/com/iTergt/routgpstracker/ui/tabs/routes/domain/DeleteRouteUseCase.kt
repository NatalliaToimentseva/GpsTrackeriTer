package com.iTergt.routgpstracker.ui.tabs.routes.domain

import android.database.sqlite.SQLiteConstraintException
import com.iTergt.routgpstracker.models.RouteModel
import com.iTergt.routgpstracker.repository.RouteRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class DeleteRouteUseCase(private val repository: RouteRepository) {

    fun deleteRoute(routeModel: RouteModel): Single<RoutesResult> {
        return Single.create<RoutesResult> { emitter ->
            try {
                repository.deleteRoute(routeModel.toRouteEntity())
                emitter.onSuccess(RoutesResult.Success)
            } catch (e: SQLiteConstraintException) {
                emitter.onError(e)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {
                RoutesResult.Error(it.message)
            }
    }
}