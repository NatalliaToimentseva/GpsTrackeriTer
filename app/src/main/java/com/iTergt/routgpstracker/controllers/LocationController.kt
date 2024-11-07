package com.iTergt.routgpstracker.controllers

import com.iTergt.routgpstracker.models.LocationModel
import io.reactivex.rxjava3.subjects.BehaviorSubject

class LocationController {

    val locationData = BehaviorSubject.create<LocationModel>()

    fun listenLocationData(): BehaviorSubject<LocationModel> = locationData
}