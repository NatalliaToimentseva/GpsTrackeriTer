package com.iTergt.routgpstracker.dataBase.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.iTergt.routgpstracker.dataBase.entity.RouteEntity

@Dao
interface RouteDao {

    @Insert
    fun saveRoute(routeEntity: RouteEntity)

    @Query("SELECT * FROM Route")
    fun getRoutes(): PagingSource<Int, RouteEntity>

    @Delete
    fun deleteRoute(routeEntity: RouteEntity)
}