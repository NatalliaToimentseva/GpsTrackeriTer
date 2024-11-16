package com.iTergt.routgpstracker.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava3.flowable
import com.iTergt.routgpstracker.dataBase.dao.RouteDao
import com.iTergt.routgpstracker.dataBase.entity.RouteEntity
import io.reactivex.rxjava3.core.Flowable

private const val INIT_PAGE_SIZE = 10
private const val INIT_LOAD_SIZE = 20

interface RouteRepository {

    fun addRoute(routeEntity: RouteEntity)

    fun getRoutes(): Flowable<PagingData<RouteEntity>>

    fun getRouteById(id: Long): RouteEntity

    fun deleteRoute(routeEntity: RouteEntity)
}

class RouteRepositoryImpl(private val routeDao: RouteDao) : RouteRepository {

    override fun addRoute(routeEntity: RouteEntity) = routeDao.saveRoute(routeEntity)

    override fun getRoutes(): Flowable<PagingData<RouteEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = INIT_PAGE_SIZE,
                prefetchDistance = INIT_LOAD_SIZE
            ),
            pagingSourceFactory = { routeDao.getRoutes() }
        ).flowable
    }

    override fun getRouteById(id: Long): RouteEntity = routeDao.getRouteById(id)

    override fun deleteRoute(routeEntity: RouteEntity) = routeDao.deleteRoute(routeEntity)
}