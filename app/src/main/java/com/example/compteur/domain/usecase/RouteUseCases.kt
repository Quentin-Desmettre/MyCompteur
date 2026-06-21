package com.example.compteur.domain.usecase

import com.example.compteur.domain.model.Route
import com.example.compteur.domain.model.RoutePoint
import com.example.compteur.domain.repository.RouteRepository
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import javax.inject.Inject

class GetRoutesUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    operator fun invoke(): Flow<List<Route>> = repository.getAllRoutes()
}

class ImportGpxUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(inputStream: InputStream, fileName: String): Result<Long> {
        var result = repository.importGpx(inputStream, fileName)
        println("result:")
        return result
    }
}

class GetRouteUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(id: Long): Route? = repository.getRouteById(id)
}

class GetRoutePointsUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    operator fun invoke(routeId: Long): Flow<List<RoutePoint>> = repository.getPointsForRoute(routeId)
}

class DeleteRouteUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(route: Route) {
        repository.deleteRoute(route)
    }
}
