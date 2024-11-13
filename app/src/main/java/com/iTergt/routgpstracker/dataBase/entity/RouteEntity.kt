package com.iTergt.routgpstracker.dataBase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iTergt.routgpstracker.models.RouteModel
import java.time.LocalDate

@Entity(tableName = "Route")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("date")
    val date: LocalDate,
    @ColumnInfo("time")
    val time: String,
    @ColumnInfo("average_speed")
    val averageSpeed: String,
    @ColumnInfo("distance")
    val distance: String,
    @ColumnInfo("geo_points")
    val geoPoints: String
) {

    fun toRouteModel(): RouteModel {
        return RouteModel(id, date, time, averageSpeed, distance, geoPoints)
    }
}