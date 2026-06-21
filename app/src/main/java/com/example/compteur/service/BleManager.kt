package com.example.compteur.service

import com.juul.kable.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalUuidApi::class)
private val HR_SERVICE_UUID = Uuid.parse("0000180d-0000-1000-8000-00805f9b34fb")
@OptIn(ExperimentalUuidApi::class)
private val HR_MEASUREMENT_CHARACTERISTIC_UUID = Uuid.parse("00002a37-0000-1000-8000-00805f9b34fb")

@OptIn(ExperimentalUuidApi::class)
private val CSCP_SERVICE_UUID = Uuid.parse("00001816-0000-1000-8000-00805f9b34fb")
@OptIn(ExperimentalUuidApi::class)
private val CSCP_MEASUREMENT_CHARACTERISTIC_UUID = Uuid.parse("00002a5b-0000-1000-8000-00805f9b34fb")

@OptIn(ExperimentalUuidApi::class)
private val CP_SERVICE_UUID = Uuid.parse("00001818-0000-1000-8000-00805f9b34fb")
@OptIn(ExperimentalUuidApi::class)
private val CP_MEASUREMENT_CHARACTERISTIC_UUID = Uuid.parse("00002a63-0000-1000-8000-00805f9b34fb")

data class BleSensorData(
    val heartRate: Int? = null,
    val cadence: Int? = null,
    val power: Int? = null,
    val speedKph: Float? = null
)

@OptIn(ExperimentalUuidApi::class)
@Singleton
class BleManager @Inject constructor(
    private val scope: CoroutineScope
) {
    private val _sensorData = MutableStateFlow(BleSensorData())
    val sensorData: StateFlow<BleSensorData> = _sensorData.asStateFlow()

    private val _connectionStates = MutableStateFlow<Map<String, State>>(emptyMap())
    val connectionStates: StateFlow<Map<String, State>> = _connectionStates.asStateFlow()

    private val _deviceCapabilities = MutableStateFlow<Map<String, Set<SensorType>>>(emptyMap())
    val deviceCapabilities: StateFlow<Map<String, Set<SensorType>>> = _deviceCapabilities.asStateFlow()

    private val activeConnections = mutableMapOf<String, Peripheral>()

    private var lastCrankRevolutions = -1
    private var lastCrankEventTime = -1
    private var lastWheelRevolutions = -1L
    private var lastWheelEventTime = -1

    fun scanDevices(types: List<SensorType>): Flow<Advertisement> {
        val serviceUuids = types.map { type ->
            when (type) {
                SensorType.HEART_RATE -> HR_SERVICE_UUID
                SensorType.CADENCE_SPEED, SensorType.SPEED, SensorType.CADENCE -> CSCP_SERVICE_UUID
                SensorType.POWER -> CP_SERVICE_UUID
            }
        }
        
        return Scanner {
            filters {
                serviceUuids.forEach { uuid ->
                    match {
                        services = listOf(uuid)
                    }
                }
            }
        }.advertisements
    }

    fun connectToDevice(macAddress: String, type: SensorType) {
        if (activeConnections.containsKey(macAddress)) {
            val peripheral = activeConnections[macAddress]!!
            // Si déjà connecté ou en cours de connexion, on ne fait rien
            // Sauf si on veut forcer la reconnexion ?
            return
        }

        val peripheral = Peripheral(macAddress)
        activeConnections[macAddress] = peripheral

        lastCrankRevolutions = -1
        lastCrankEventTime = -1
        lastWheelRevolutions = -1L
        lastWheelEventTime = -1

        scope.launch {
            try {
                // Monitor connection state
                peripheral.state.collect { state ->
                    _connectionStates.update { it + (macAddress to state) }
                    if (state is State.Disconnected) {
                        // Nettoyage si déconnecté (si le management est décentralisé)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error monitoring state for $macAddress")
            }
        }

        scope.launch {
            try {
                peripheral.connect()
                observeSensor(peripheral, type)
            } catch (e: Exception) {
                Timber.e(e, "Failed to connect to $macAddress")
                activeConnections.remove(macAddress)
                _connectionStates.update { it - macAddress }
            }
        }
    }

    fun disconnectDevice(macAddress: String) {
        val peripheral = activeConnections[macAddress]
        peripheral?.let {
            scope.launch {
                try {
                    it.disconnect()
                } catch (e: Exception) {
                    Timber.e(e, "Error disconnecting $macAddress")
                } finally {
                    activeConnections.remove(macAddress)
                    _connectionStates.update { it - macAddress }
                }
            }
        }
    }

    private suspend fun observeSensor(peripheral: Peripheral, type: SensorType) {
        val (serviceUuid, charUuid) = when (type) {
            SensorType.HEART_RATE -> HR_SERVICE_UUID to HR_MEASUREMENT_CHARACTERISTIC_UUID
            SensorType.CADENCE_SPEED, SensorType.SPEED, SensorType.CADENCE -> CSCP_SERVICE_UUID to CSCP_MEASUREMENT_CHARACTERISTIC_UUID
            SensorType.POWER -> CP_SERVICE_UUID to CP_MEASUREMENT_CHARACTERISTIC_UUID
        }

        peripheral.observe(characteristicOf(serviceUuid, charUuid))
            .collect { data ->
                decodeData(peripheral.identifier, data, type)
            }
    }

    private fun decodeData(deviceId: String, data: ByteArray, type: SensorType) {
        when (type) {
            SensorType.HEART_RATE -> {
                val hr = if (data[0].toInt() and 0x01 == 0) data[1].toInt() and 0xFF else (data[1].toInt() and 0xFF) or ((data[2].toInt() and 0xFF) shl 8)
                _sensorData.update { it.copy(heartRate = hr) }
            }
            SensorType.CADENCE_SPEED, SensorType.SPEED, SensorType.CADENCE -> {
                if (data.isEmpty()) return
                val flags = data[0].toInt()
                val hasWheel = (flags and 0x01) != 0
                val hasCrank = (flags and 0x02) != 0

                // Update device capabilities for auto-detection
                val detected = mutableSetOf<SensorType>()
                if (hasWheel) detected.add(SensorType.SPEED)
                if (hasCrank) detected.add(SensorType.CADENCE)
                if (hasWheel && hasCrank) detected.add(SensorType.CADENCE_SPEED)
                
                if (detected.isNotEmpty()) {
                    _deviceCapabilities.update { it + (deviceId to detected) }
                }

                var offset = 1

                var newSpeedKph: Float? = null
                var newCadence: Int? = null

                if (hasWheel && data.size >= offset + 6) {
                    val wheelRevs = (data[offset].toLong() and 0xFF) or
                            ((data[offset + 1].toLong() and 0xFF) shl 8) or
                            ((data[offset + 2].toLong() and 0xFF) shl 16) or
                            ((data[offset + 3].toLong() and 0xFF) shl 24)
                    offset += 4
                    val wheelTime = (data[offset].toInt() and 0xFF) or ((data[offset + 1].toInt() and 0xFF) shl 8)
                    offset += 2

                    if (lastWheelRevolutions >= 0L && lastWheelEventTime >= 0) {
                        var timeDiff = wheelTime - lastWheelEventTime
                        if (timeDiff < 0) timeDiff += 65536
                        
                        var revDiff = wheelRevs - lastWheelRevolutions
                        if (revDiff < 0L) revDiff += 4294967296L
                        
                        if (timeDiff > 0) {
                            val timeSeconds = timeDiff / 1024.0f
                            val wheelCircumferenceMeters = 2.1f // Default ~700x25c wheel
                            val distanceMeters = revDiff * wheelCircumferenceMeters
                            val speedMs = distanceMeters / timeSeconds
                            newSpeedKph = speedMs * 3.6f
                        }
                    }
                    lastWheelRevolutions = wheelRevs
                    lastWheelEventTime = wheelTime
                }

                if (hasCrank && data.size >= offset + 4) {
                    val crankRevs = (data[offset].toInt() and 0xFF) or ((data[offset + 1].toInt() and 0xFF) shl 8)
                    offset += 2
                    val crankTime = (data[offset].toInt() and 0xFF) or ((data[offset + 1].toInt() and 0xFF) shl 8)
                    offset += 2

                    if (lastCrankRevolutions >= 0 && lastCrankEventTime >= 0) {
                        var timeDiff = crankTime - lastCrankEventTime
                        if (timeDiff < 0) timeDiff += 65536
                        
                        var revDiff = crankRevs - lastCrankRevolutions
                        if (revDiff < 0) revDiff += 65536
                        
                        if (timeDiff > 0) {
                            val timeSeconds = timeDiff / 1024.0f
                            newCadence = (revDiff / timeSeconds * 60).toInt()
                        }
                    }
                    lastCrankRevolutions = crankRevs
                    lastCrankEventTime = crankTime
                }

                if (newCadence != null || newSpeedKph != null) {
                    _sensorData.update { 
                        it.copy(
                            cadence = newCadence ?: it.cadence,
                            speedKph = newSpeedKph ?: it.speedKph
                        ) 
                    }
                }
            }
            SensorType.POWER -> {
                val power = (data[2].toInt() and 0xFF) or ((data[3].toInt() and 0xFF) shl 8)
                _sensorData.update { it.copy(power = power) }
            }
        }
    }

    fun disconnectAll() {
        scope.launch {
            activeConnections.values.forEach { it.disconnect() }
            activeConnections.clear()
        }
    }
}

enum class SensorType { HEART_RATE, CADENCE_SPEED, SPEED, CADENCE, POWER }
