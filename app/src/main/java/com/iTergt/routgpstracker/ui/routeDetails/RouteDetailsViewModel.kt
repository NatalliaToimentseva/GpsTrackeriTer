package com.iTergt.routgpstracker.ui.routeDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iTergt.routgpstracker.models.RouteModel
import com.iTergt.routgpstracker.ui.routeDetails.domain.LoadRouteUseCase
import com.iTergt.routgpstracker.ui.routeDetails.domain.RouteDetailsResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.osmdroid.util.GeoPoint

class RouteDetailsViewModel(
    private val loadRouteUseCase: LoadRouteUseCase
) : ViewModel() {

    private val _route: MutableLiveData<RouteModel> = MutableLiveData<RouteModel>()
    val route: LiveData<RouteModel> = _route

    private val _startPoint: MutableLiveData<GeoPoint> = MutableLiveData<GeoPoint>()
    val startPoint: LiveData<GeoPoint> = _startPoint

    private val disposable = CompositeDisposable()
    var onError: ((message: String?) -> Unit)? = null

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun loadRoute(id: Long) {
        disposable.add(
            loadRouteUseCase.loadRoute(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        handleResult(result)
                    },
                    { error ->
                        handleResult(RouteDetailsResult.Error(error.message))
                    }
                )
        )
    }

    fun setStartPoint(point: GeoPoint) {
        _startPoint.value = point
    }

    private fun handleResult(result: RouteDetailsResult) {
        when (result) {
            is RouteDetailsResult.Error -> onError?.invoke(result.message)
            is RouteDetailsResult.Success -> _route.value = result.route
        }
    }
}