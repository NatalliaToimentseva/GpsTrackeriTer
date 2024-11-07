package com.iTergt.routgpstracker.ui.home

import android.util.Log
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

class HomeViewModel(private val locationController: LocationController) : ViewModel() {

    private val _isPermissionGranted: MutableLiveData<Boolean> = MutableLiveData(true)
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    private val _isServiceRunning: MutableLiveData<Boolean> = MutableLiveData(false)
    val isServiceRunning: LiveData<Boolean> = _isServiceRunning

    private val _timePassed: MutableLiveData<String> = MutableLiveData<String>(START_TIME)
    val timePassed: LiveData<String> = _timePassed

    private val _locationData: MutableLiveData<LocationModel> = MutableLiveData<LocationModel>()
    val locationData: LiveData<LocationModel> = _locationData

    private val disposable = CompositeDisposable()
    private var timer: Timer? = null

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
        disposable.clear()
    }

    init {
        disposable.add(
            locationController.listenLocationData().subscribe{ location ->
                _locationData.postValue(location)
                Log.d("AAA", "VM _locationData = ${_locationData.value}")
            }
        )
    }

    fun setPermissionStatus(isGranted: Boolean) {
        _isPermissionGranted.value = isGranted
    }

    fun setIsServiceRunning(isRunning: Boolean) {
        _isServiceRunning.value = isRunning
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