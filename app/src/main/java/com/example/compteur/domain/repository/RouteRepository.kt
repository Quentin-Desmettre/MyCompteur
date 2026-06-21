package com.example.compteur.domain.repository

import com.example.compteur.domain.model.Route
import com.example.compteur.domain.model.RoutePoint
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface RouteRepository {
    fun getAllRoutes(): Flow<List<Route>>
    suspend fun getRouteById(id: Long): Route?
    fun getPointsForRoute(routeId: Long): Flow<List<RoutePoint>>
    suspend fun importGpx(inputStream: InputStream, fileName: String): Result<Long>
    suspend fun deleteRoute(route: Route)
}
