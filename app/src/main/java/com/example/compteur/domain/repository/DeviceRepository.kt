package com.example.compteur.domain.repository

import com.example.compteur.data.db.entity.SynchronizedDeviceEntity
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getAllSynchronizedDevices(): Flow<List<SynchronizedDeviceEntity>>
    suspend fun saveDevice(device: SynchronizedDeviceEntity)
    suspend fun deleteDevice(device: SynchronizedDeviceEntity)
    suspend fun getDeviceByAddress(macAddress: String): SynchronizedDeviceEntity?
}
