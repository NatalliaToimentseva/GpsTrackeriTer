package com.iTergt.routgpstracker.ui.tabs.home.domain

import android.database.sqlite.SQLiteConstraintException
import com.iTergt.routgpstracker.models.RouteModel
import com.iTergt.routgpstracker.repository.RouteRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class SaveRouteUseCase(private val repository: RouteRepository) {

    fun saveRoute(routeModel: RouteModel): Single<HomeResult> {
        return Single.create<HomeResult> { emitter ->
            try {
                repository.addRoute(routeModel.toRouteEntity())
                emitter.onSuccess(HomeResult.Success)
            } catch (e: SQLiteConstraintException) {
                emitter.onError(e)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn {
                HomeResult.Error(it.message)
            }
    }
}