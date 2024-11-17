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

    fun getRoutes(uid: String): Flowable<PagingData<RouteEntity>>

    fun getRouteById(id: Long, uid: String): RouteEntity

    fun deleteRoute(routeEntity: RouteEntity)
}

class RouteRepositoryImpl(private val routeDao: RouteDao) : RouteRepository {

    override fun addRoute(routeEntity: RouteEntity) = routeDao.saveRoute(routeEntity)

    override fun getRoutes(uid: String): Flowable<PagingData<RouteEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = INIT_PAGE_SIZE,
                prefetchDistance = INIT_LOAD_SIZE
            ),
            pagingSourceFactory = { routeDao.getRoutes(uid) }
        ).flowable
    }

    override fun getRouteById(id: Long, uid: String): RouteEntity = routeDao.getRouteById(id, uid)

    override fun deleteRoute(routeEntity: RouteEntity) = routeDao.deleteRoute(routeEntity)
}