package com.example.compteur.data.db.dao

import androidx.room.*
import com.example.compteur.data.db.entity.GpsPointEntity
import com.example.compteur.data.db.entity.SensorDataEntity
import com.example.compteur.data.db.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: SessionEntity): Long

    @Update
    suspend fun update(session: SessionEntity)

    @Delete
    suspend fun delete(session: SessionEntity)

    @Query("SELECT * FROM sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): SessionEntity?
}

@Dao
interface GpsPointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(points: List<GpsPointEntity>)

    @Query("SELECT * FROM gps_points WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    fun getPointsForSession(sessionId: Long): Flow<List<GpsPointEntity>>
}

@Dao
interface SensorDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(data: List<SensorDataEntity>)

    @Query("SELECT * FROM sensor_data WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    fun getDataForSession(sessionId: Long): Flow<List<SensorDataEntity>>
}
