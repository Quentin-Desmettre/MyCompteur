package com.example.compteur.data.db.entity

import androidx.room.*

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routeId: Long? = null,
    val startedAt: Long, // Temps epoch en millisecondes
    val endedAt: Long? = null,
    val totalDistanceMeters: Float = 0f,
    val avgSpeedKph: Float = 0f,
    val maxSpeedKph: Float = 0f,
    val avgPowerWatts: Int? = null,
    val avgHeartRateBpm: Int? = null,
    val totalAscentMeters: Float = 0f
)

@Entity(
    tableName = "gps_points",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class GpsPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Float,
    val speedMps: Float,
    val timestampMs: Long
)

@Entity(tableName = "sensor_data",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class SensorDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val timestampMs: Long,
    val powerWatts: Int? = null,
    val cadenceRpm: Int? = null,
    val heartRateBpm: Int? = null,
    val bleSpeedKph: Float? = null
)

@Entity(tableName = "synchronized_devices")
data class SynchronizedDeviceEntity(
    @PrimaryKey val macAddress: String,
    val name: String,
    val type: String // SensorType enum name
)
