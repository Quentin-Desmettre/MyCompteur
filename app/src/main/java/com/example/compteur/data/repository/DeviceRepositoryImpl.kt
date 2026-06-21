package com.example.compteur.data.repository

import com.example.compteur.data.db.dao.DeviceDao
import com.example.compteur.data.db.entity.SynchronizedDeviceEntity
import com.example.compteur.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao
) : DeviceRepository {
    override fun getAllSynchronizedDevices(): Flow<List<SynchronizedDeviceEntity>> {
        return deviceDao.getAllDevices()
    }

    override suspend fun saveDevice(device: SynchronizedDeviceEntity) {
        deviceDao.insertDevice(device)
    }

    override suspend fun deleteDevice(device: SynchronizedDeviceEntity) {
        deviceDao.deleteDevice(device)
    }

    override suspend fun getDeviceByAddress(macAddress: String): SynchronizedDeviceEntity? {
        return deviceDao.getDeviceByAddress(macAddress)
    }
}
