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

    @Query("SELECT * FROM Route WHERE user_uid = :uid")
    fun getRoutes(uid: String): PagingSource<Int, RouteEntity>

    @Query("SELECT * FROM Route WHERE id = :id AND user_uid = :uid")
    fun getRouteById(id: Long, uid: String): RouteEntity

    @Delete
    fun deleteRoute(routeEntity: RouteEntity)
}