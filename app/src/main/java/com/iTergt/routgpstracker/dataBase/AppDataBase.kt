package com.iTergt.routgpstracker.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.iTergt.routgpstracker.dataBase.dao.RouteDao
import com.iTergt.routgpstracker.dataBase.entity.RouteEntity
import com.iTergt.routgpstracker.utils.DateConverter

@Database(
    version = 1,
    entities = [RouteEntity::class]
)
@TypeConverters(value = [DateConverter::class])
abstract class AppDataBase : RoomDatabase() {

    abstract fun getRouteDao(): RouteDao
}