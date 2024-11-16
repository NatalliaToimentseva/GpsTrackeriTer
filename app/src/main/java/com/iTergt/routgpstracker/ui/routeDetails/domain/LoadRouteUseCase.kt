package com.iTergt.routgpstracker.ui.routeDetails.domain

import android.database.sqlite.SQLiteConstraintException
import com.iTergt.routgpstracker.repository.RouteRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class LoadRouteUseCase(private val repository: RouteRepository) {

    fun loadRoute(id: Long): Single<RouteDetailsResult> {
        return Single.create<RouteDetailsResult> { emitter ->
            try {
                val route = repository.getRouteById(id).toRouteModel()
                emitter.onSuccess(RouteDetailsResult.Success(route))
            } catch (e: SQLiteConstraintException) {
                emitter.onError(e)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {
                RouteDetailsResult.Error(it.message)
            }
    }
}