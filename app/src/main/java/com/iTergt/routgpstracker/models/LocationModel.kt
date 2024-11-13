package com.iTergt.routgpstracker.models

import org.osmdroid.util.GeoPoint

data class LocationModel(
    val speed: String,
    val averageSpeed:String,
    val distance: String,
    val geoPointsList: ArrayList<GeoPoint>
)