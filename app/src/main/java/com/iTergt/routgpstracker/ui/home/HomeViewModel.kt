package com.iTergt.routgpstracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {

    private val _isPermissionGranted: MutableLiveData<Boolean> = MutableLiveData(true)
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    fun setPermissionStatus(isGranted: Boolean) {
        _isPermissionGranted.value = isGranted
    }
}