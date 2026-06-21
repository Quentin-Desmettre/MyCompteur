package com.example.compteur.domain.repository

import com.example.compteur.data.db.entity.GpsPointEntity
import com.example.compteur.data.db.entity.SensorDataEntity
import com.example.compteur.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getAllSessions(): Flow<List<Session>>
    suspend fun getSessionById(id: Long): Session?
    suspend fun startSession(routeId: Long?): Long
    suspend fun saveSession(session: Session)
    suspend fun deleteSession(session: Session)
    fun getGpsPointsForSession(sessionId: Long): Flow<List<GpsPointEntity>>
    fun getSensorDataForSession(sessionId: Long): Flow<List<SensorDataEntity>>
}
