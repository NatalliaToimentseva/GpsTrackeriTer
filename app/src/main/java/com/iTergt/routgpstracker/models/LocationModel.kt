package com.iTergt.routgpstracker.models

import org.osmdroid.util.GeoPoint

data class LocationModel(
    val speed: String = "0.0",
    val averageSpeed: String = "0.0",
    val distance: String = "0.0",
    val geoPointsList: ArrayList<GeoPoint> = arrayListOf()
)