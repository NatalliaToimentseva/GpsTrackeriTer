package com.iTergt.routgpstracker.utils

import org.osmdroid.util.GeoPoint

fun geoPointsConvertToString(geoPoints: List<GeoPoint>): String {
    val geoPointsStringBuilder = StringBuilder()
    geoPoints.forEach { geoPoint ->
        geoPointsStringBuilder.append("${geoPoint.latitude},${geoPoint.longitude}/")
    }
    return geoPointsStringBuilder.toString()
}