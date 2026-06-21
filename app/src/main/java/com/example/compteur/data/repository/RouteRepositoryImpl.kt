package com.example.compteur.data.repository

import com.example.compteur.data.db.dao.RouteDao
import com.example.compteur.data.db.dao.RoutePointDao
import com.example.compteur.data.db.entity.RouteEntity
import com.example.compteur.data.db.entity.RoutePointEntity
import com.example.compteur.data.gpx.GpxParser
import com.example.compteur.domain.model.Route
import com.example.compteur.domain.model.RoutePoint
import com.example.compteur.domain.repository.RouteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val routeDao: RouteDao,
    private val routePointDao: RoutePointDao,
    private val gpxParser: GpxParser
) : RouteRepository {

    override fun getAllRoutes(): Flow<List<Route>> {
        return routeDao.getAllRoutes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getRouteById(id: Long): Route? {
        return routeDao.getRouteById(id)?.toDomain()
    }

    override fun getPointsForRoute(routeId: Long): Flow<List<RoutePoint>> {
        return routePointDao.getPointsForRoute(routeId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun importGpx(inputStream: InputStream, fileName: String): Result<Long> = runCatching {
        // 1. Initialiser une route temporaire pour obtenir l'ID
        val initialRoute = RouteEntity(
            name = fileName,
            dateImported = System.currentTimeMillis()
        )
        val routeId = routeDao.insert(initialRoute)

        // Important: we need the entity with the correct ID for updates or deletion
        val persistentRoute = initialRoute.copy(id = routeId)

        try {
            // 2. Parser le GPX avec l'ID de la route
            val gpxData = gpxParser.parse(inputStream, routeId)
            android.util.Log.d("RouteRepository", "GPX parsed: ${gpxData.name}, ${gpxData.points.size} points found")

            if (gpxData.points.isEmpty()) {
                android.util.Log.e("RouteRepository", "Import failed: No points found in GPX")
                routeDao.delete(persistentRoute)
                throw IllegalArgumentException("No points found in GPX file")
            }

            var distance = 0f
            var ascent = 0f
            for (i in 1 until gpxData.points.size) {
                val p1 = gpxData.points[i - 1]
                val p2 = gpxData.points[i]
                
                val results = FloatArray(1)
                android.location.Location.distanceBetween(
                    p1.latitude, p1.longitude,
                    p2.latitude, p2.longitude,
                    results
                )
                distance += results[0]
                
                if (p1.altitudeMeters != null && p2.altitudeMeters != null) {
                    val diff = p2.altitudeMeters - p1.altitudeMeters
                    if (diff > 0) {
                        ascent += diff
                    }
                }
            }

            android.util.Log.d("RouteRepository", "Impact calculated: distance=${distance}m, ascent=${ascent}m")

            // 3. Mise à jour du nom de la route et insertion des points par lots
            val updatedRoute = persistentRoute.copy(
                name = gpxData.name,
                totalDistanceMeters = distance,
                totalAscentMeters = ascent
            )
            routeDao.update(updatedRoute)
            
            android.util.Log.d("RouteRepository", "Inserting ${gpxData.points.size} points into DB")
            routePointDao.insertBatch(gpxData.points)
            
            android.util.Log.d("RouteRepository", "Import successful for routeId: $routeId")
            routeId
        } catch (e: Exception) {
            // Nettoyage si erreur pendant le parsing
            routeDao.delete(persistentRoute)
            throw e
        }
    }

    override suspend fun deleteRoute(route: Route) {
        val entity = RouteEntity(
            id = route.id,
            name = route.name,
            totalDistanceMeters = route.distanceMeters,
            totalAscentMeters = route.ascentMeters,
            dateImported = route.dateImported
        )
        routeDao.delete(entity)
    }

    private fun RouteEntity.toDomain() = Route(
        id = id,
        name = name,
        distanceMeters = totalDistanceMeters,
        ascentMeters = totalAscentMeters,
        dateImported = dateImported
    )

    private fun RoutePointEntity.toDomain() = RoutePoint(
        latitude = latitude,
        longitude = longitude,
        altitudeMeters = altitudeMeters
    )
}
