package com.example.compteur.data.db.dao

import androidx.room.*
import com.example.compteur.data.db.entity.SynchronizedDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM synchronized_devices")
    fun getAllDevices(): Flow<List<SynchronizedDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: SynchronizedDeviceEntity)

    @Delete
    suspend fun deleteDevice(device: SynchronizedDeviceEntity)

    @Query("SELECT * FROM synchronized_devices WHERE macAddress = :macAddress")
    suspend fun getDeviceByAddress(macAddress: String): SynchronizedDeviceEntity?
}
