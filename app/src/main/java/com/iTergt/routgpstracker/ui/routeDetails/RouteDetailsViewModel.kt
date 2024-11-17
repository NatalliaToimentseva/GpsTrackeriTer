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

/**
 * ViewModel class for managing the route details in the application.
 *
 * This ViewModel is responsible for loading route data using the provided
 * [LoadRouteUseCase] and exposing the relevant data to the UI through LiveData.
 *
 * @property loadRouteUseCase The use case for loading route data by ID.
 */
class RouteDetailsViewModel(
    private val loadRouteUseCase: LoadRouteUseCase
) : ViewModel() {

    /**
     * A private MutableLiveData object that holds the current route details.
     */
    private val _route: MutableLiveData<RouteModel> = MutableLiveData<RouteModel>()

    /**
     * Public LiveData that exposes the route details to the UI.
     */
    val route: LiveData<RouteModel> = _route

    /**
     * A private MutableLiveData object that holds the starting point of the route.
     */
    private val _startPoint: MutableLiveData<GeoPoint> = MutableLiveData<GeoPoint>()

    /**
     * Public LiveData that exposes the starting point to the UI.
     */
    val startPoint: LiveData<GeoPoint> = _startPoint

    /**
     * A CompositeDisposable to manage RxJava subscriptions and prevent memory leaks.
     */
    private val disposable = CompositeDisposable()

    /**
     * A callback function that can be set to handle error messages.
     */
    var onError: ((message: String?) -> Unit)? = null

    /**
     * Clears the disposables when the ViewModel is cleared to prevent memory leaks.
     */
    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    /**
     * Loads the route details for a given route ID.
     *
     * This function subscribes to the result of the loadRoute use case,
     * handles the result on the appropriate threads, and updates the
     * LiveData properties accordingly.
     *
     * @param id The ID of the route to load.
     */
    fun loadRoute(id: Long, uid: String) {
        disposable.add(
            loadRouteUseCase.loadRoute(id, uid)
                .subscribeOn(Schedulers.io()) // Perform the operation on the IO thread
                .observeOn(AndroidSchedulers.mainThread()) // Observe the result on the main thread
                .subscribe(
                    { result ->
                        handleResult(result) // Handle successful result
                    },
                    { error ->
                        handleResult(RouteDetailsResult.Error(error.message)) // Handle error
                    }
                )
        )
    }

    /**
     * Sets the starting point of the route.
     *
     * This function updates the starting point LiveData with the provided GeoPoint.
     *
     * @param point The starting point of the route as a GeoPoint.
     */
    fun setStartPoint(point: GeoPoint) {
        _startPoint.value = point
    }

    /**
     * Handles the result of the route loading operation.
     *
     * This private function processes the result and updates the LiveData
     * properties or invokes the error callback as necessary.
     *
     * @param result The result of the route loading operation.
     */
    private fun handleResult(result: RouteDetailsResult) {
        when (result) {
            is RouteDetailsResult.Error -> onError?.invoke(result.message) // Invoke error callback
            is RouteDetailsResult.Success -> _route.value = result.route // Update route LiveData
        }
    }
}