package com.iTergt.routgpstracker.utils

import org.osmdroid.util.GeoPoint

private const val DIVIDER = "/"
private const val POINT_DIVIDER = ","

fun geoPointsConvertToString(geoPoints: List<GeoPoint>): String {
    val geoPointsStringBuilder = StringBuilder()
    geoPoints.forEach { geoPoint ->
        geoPointsStringBuilder.append("${geoPoint.latitude}$POINT_DIVIDER${geoPoint.longitude}$DIVIDER")
    }
    return geoPointsStringBuilder.toString()
}

fun geoPointsConvertFromString(geoPointsString: String): List<GeoPoint> {
    val geoPointsList: ArrayList<GeoPoint> = arrayListOf()

    geoPointsString.split(DIVIDER).forEach { geoPointString ->
        if (geoPointString.isNotEmpty()) {
            val points = geoPointString.split(POINT_DIVIDER)
            geoPointsList.add(
                GeoPoint(
                    points[0].toDouble(),
                    points[1].toDouble()
                )
            )
        }
    }
    return geoPointsList
}