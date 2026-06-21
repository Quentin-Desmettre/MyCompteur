package com.example.compteur.data.db.dao

import androidx.room.*
import com.example.compteur.data.db.entity.RouteEntity
import com.example.compteur.data.db.entity.RoutePointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert
    suspend fun insert(route: RouteEntity): Long

    @Update
    suspend fun update(route: RouteEntity)

    @Delete
    suspend fun delete(route: RouteEntity)

    @Query("SELECT * FROM routes ORDER BY dateImported DESC")
    fun getAllRoutes(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRouteById(routeId: Long): RouteEntity?
}

@Dao
interface RoutePointDao {
    @Insert
    suspend fun insertBatch(points: List<RoutePointEntity>)

    @Query("SELECT * FROM route_points WHERE routeId = :routeId ORDER BY sequenceOrder ASC")
    fun getPointsForRoute(routeId: Long): Flow<List<RoutePointEntity>>

    @Query("SELECT * FROM route_points WHERE routeId = :routeId ORDER BY sequenceOrder ASC")
    suspend fun getPointsForRouteSync(routeId: Long): List<RoutePointEntity>
}
