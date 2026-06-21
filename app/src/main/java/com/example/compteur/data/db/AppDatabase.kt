package com.example.compteur.data.db

import androidx.room.*
import com.example.compteur.data.db.dao.*
import com.example.compteur.data.db.entity.*

@Database(
    entities = [
        SessionEntity::class,
        GpsPointEntity::class,
        SensorDataEntity::class,
        RouteEntity::class,
        RoutePointEntity::class,
        SynchronizedDeviceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun gpsPointDao(): GpsPointDao
    abstract fun sensorDataDao(): SensorDataDao
    abstract fun routeDao(): RouteDao
    abstract fun routePointDao(): RoutePointDao
    abstract fun deviceDao(): DeviceDao
}
