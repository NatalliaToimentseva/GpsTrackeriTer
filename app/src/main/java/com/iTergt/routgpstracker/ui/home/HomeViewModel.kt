package com.iTergt.routgpstracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iTergt.routgpstracker.controllers.LocationController
import com.iTergt.routgpstracker.models.LocationModel
import com.iTergt.routgpstracker.service.LocationService
import com.iTergt.routgpstracker.utils.convertTimeToString
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.Timer
import java.util.TimerTask

private const val START_TIME = "00:00:00"
private const val TIMER_DELAY = 1000L
private const val TIMER_PERIOD = 1000L

class HomeViewModel(locationController: LocationController) : ViewModel() {

    private val _isPermissionGranted: MutableLiveData<Boolean> = MutableLiveData(true)
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    private val _isServiceRunning: MutableLiveData<Boolean> = MutableLiveData(false)
    val isServiceRunning: LiveData<Boolean> = _isServiceRunning

    private val _timePassed: MutableLiveData<String> = MutableLiveData<String>(START_TIME)
    val timePassed: LiveData<String> = _timePassed

    private val _locationData: MutableLiveData<LocationModel> = MutableLiveData<LocationModel>()
    val locationData: LiveData<LocationModel> = _locationData

    private val _isMapInitialized: MutableLiveData<Boolean> = MutableLiveData(false)
    val isMapInitialized: LiveData<Boolean> = _isMapInitialized

    private val _isFirstStart: MutableLiveData<Boolean> = MutableLiveData(true)
    val isFirstStart: LiveData<Boolean> = _isFirstStart

    private val _isShowDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    val isShowDialog: LiveData<Boolean> = _isShowDialog

    private val disposable = CompositeDisposable()
    private var timer: Timer? = null

    init {
        disposable.add(
            locationController.listenLocationData().subscribe { location ->
                _locationData.postValue(location)
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
        disposable.clear()
    }

    fun setPermissionStatus(isGranted: Boolean) {
        _isPermissionGranted.value = isGranted
    }

    fun setIsServiceRunning(isRunning: Boolean) {
        _isServiceRunning.value = isRunning
    }

    fun setIsMapInitialized(isInitialized: Boolean) {
        _isMapInitialized.value = isInitialized
    }

    fun setIsFirstStart(isFirst: Boolean) {
        _isFirstStart.value = isFirst
    }

    fun setShowDialog(isShow: Boolean) {
        _isShowDialog.value = isShow
    }

    fun startTimer() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {

            override fun run() {
                _timePassed.postValue(
                    convertTimeToString(System.currentTimeMillis() - LocationService.startTime)
                )
            }
        }, TIMER_DELAY, TIMER_PERIOD)
    }

    fun stopTimer() {
        timer?.cancel()
    }
}