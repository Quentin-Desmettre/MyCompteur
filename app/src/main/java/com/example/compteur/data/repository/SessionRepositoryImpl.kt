package com.example.compteur.data.repository

import com.example.compteur.data.db.dao.GpsPointDao
import com.example.compteur.data.db.dao.SensorDataDao
import com.example.compteur.data.db.dao.SessionDao
import com.example.compteur.data.db.entity.GpsPointEntity
import com.example.compteur.data.db.entity.SensorDataEntity
import com.example.compteur.data.db.entity.SessionEntity
import com.example.compteur.domain.model.Session
import com.example.compteur.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val gpsPointDao: GpsPointDao,
    private val sensorDataDao: SensorDataDao
) : SessionRepository {

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSessionById(id: Long): Session? {
        return sessionDao.getSessionById(id)?.toDomain()
    }

    override suspend fun startSession(routeId: Long?): Long {
        val session = SessionEntity(
            routeId = routeId,
            startedAt = System.currentTimeMillis()
        )
        return sessionDao.insert(session)
    }

    override suspend fun saveSession(session: Session) {
        val entity = SessionEntity(
            id = session.id,
            routeId = session.routeId,
            startedAt = session.startedAt,
            endedAt = session.endedAt,
            totalDistanceMeters = session.distanceMeters,
            avgSpeedKph = session.avgSpeedKph,
            maxSpeedKph = session.maxSpeedKph,
            avgPowerWatts = session.avgPowerWatts,
            avgHeartRateBpm = session.avgHeartRateBpm,
            totalAscentMeters = session.ascentMeters
        )
        sessionDao.update(entity)
    }

    override suspend fun deleteSession(session: Session) {
        val entity = SessionEntity(id = session.id, startedAt = session.startedAt)
        sessionDao.delete(entity)
    }

    override fun getGpsPointsForSession(sessionId: Long): Flow<List<GpsPointEntity>> {
        return gpsPointDao.getPointsForSession(sessionId)
    }

    override fun getSensorDataForSession(sessionId: Long): Flow<List<SensorDataEntity>> {
        return sensorDataDao.getDataForSession(sessionId)
    }

    private fun SessionEntity.toDomain() = Session(
        id = id,
        routeId = routeId,
        startedAt = startedAt,
        endedAt = endedAt,
        distanceMeters = totalDistanceMeters,
        avgSpeedKph = avgSpeedKph,
        maxSpeedKph = maxSpeedKph,
        avgPowerWatts = avgPowerWatts,
        avgHeartRateBpm = avgHeartRateBpm,
        ascentMeters = totalAscentMeters
    )
}
