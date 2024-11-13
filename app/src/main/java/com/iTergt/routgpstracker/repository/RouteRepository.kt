package com.iTergt.routgpstracker.repository

import androidx.paging.PagingSource
import com.iTergt.routgpstracker.dataBase.dao.RouteDao
import com.iTergt.routgpstracker.dataBase.entity.RouteEntity

interface RouteRepository {

    fun addRoute(routeEntity: RouteEntity)

    fun getRoutes(): PagingSource<Int, RouteEntity>

    fun deleteRoute(routeEntity: RouteEntity)
}

class RouteRepositoryImpl(private val routeDao: RouteDao) : RouteRepository {

    override fun addRoute(routeEntity: RouteEntity) = routeDao.saveRoute(routeEntity)

    override fun getRoutes(): PagingSource<Int, RouteEntity> = routeDao.getRoutes()

    override fun deleteRoute(routeEntity: RouteEntity) = routeDao.deleteRoute(routeEntity)
}