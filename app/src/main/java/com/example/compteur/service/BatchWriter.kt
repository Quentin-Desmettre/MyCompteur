package com.example.compteur.service

import com.example.compteur.data.db.dao.GpsPointDao
import com.example.compteur.data.db.dao.SensorDataDao
import com.example.compteur.data.db.entity.GpsPointEntity
import com.example.compteur.data.db.entity.SensorDataEntity
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue

class BatchWriter(
    private val gpsDao: GpsPointDao,
    private val sensorDataDao: SensorDataDao,
    private val scope: CoroutineScope,
    private val sessionId: Long,
    private val batchSize: Int = 20,
    private val flushIntervalMs: Long = 20000L // 20 secondes
) {
    private val gpsQueue = ConcurrentLinkedQueue<GpsPointEntity>()
    private val sensorQueue = ConcurrentLinkedQueue<SensorDataEntity>()
    private var flushJob: Job? = null

    init {
        startFlushTimer()
    }

    fun addGpsPoint(point: GpsPointEntity) {
        gpsQueue.add(point)
        checkFlush()
    }

    fun addSensorData(data: SensorDataEntity) {
        sensorQueue.add(data)
        checkFlush()
    }

    private fun checkFlush() {
        if (gpsQueue.size >= batchSize || sensorQueue.size >= batchSize) {
            flush()
        }
    }

    private fun startFlushTimer() {
        flushJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(flushIntervalMs)
                flush()
            }
        }
    }

    fun flush() {
        scope.launch(Dispatchers.IO) {
            val gpsToInsert = mutableListOf<GpsPointEntity>()
            while (gpsQueue.isNotEmpty() && gpsToInsert.size < batchSize * 2) {
                gpsQueue.poll()?.let { gpsToInsert.add(it) }
            }
            if (gpsToInsert.isNotEmpty()) {
                gpsDao.insertBatch(gpsToInsert)
            }

            val sensorToInsert = mutableListOf<SensorDataEntity>()
            while (sensorQueue.isNotEmpty() && sensorToInsert.size < batchSize * 2) {
                sensorQueue.poll()?.let { sensorToInsert.add(it) }
            }
            if (sensorToInsert.isNotEmpty()) {
                sensorDataDao.insertBatch(sensorToInsert)
            }
        }
    }

    fun stop() {
        flushJob?.cancel()
        flush() // Final flush
    }
}
