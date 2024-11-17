package com.iTergt.routgpstracker.models

import com.iTergt.routgpstracker.dataBase.entity.RouteEntity
import java.time.LocalDate

data class RouteModel(
    val id: Long,
    val date: LocalDate,
    val time: String,
    val averageSpeed: String,
    val distance: String,
    val geoPoints: String,
    val userUid: String
) {

    fun toRouteEntity(): RouteEntity {
        return RouteEntity(id, date, time, averageSpeed, distance, geoPoints, userUid)
    }
}

