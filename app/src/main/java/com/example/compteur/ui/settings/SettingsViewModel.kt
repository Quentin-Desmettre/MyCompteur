package com.example.compteur.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compteur.service.BleManager
import com.example.compteur.service.SensorType
import com.example.compteur.domain.repository.DeviceRepository
import com.example.compteur.data.db.entity.SynchronizedDeviceEntity
import com.juul.kable.Advertisement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.compteur.data.repository.SettingsRepository
import com.example.compteur.data.repository.MapStyle
import com.example.compteur.data.repository.HrCalculationMode
import com.example.compteur.service.OfflineMapManager
import com.example.compteur.service.HeartRateZoneService
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.offline.OfflineRegion
import com.example.compteur.domain.repository.StravaRepository

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val bleManager: BleManager,
    private val deviceRepository: DeviceRepository,
    private val settingsRepository: SettingsRepository,
    private val offlineMapManager: OfflineMapManager,
    private val heartRateZoneService: HeartRateZoneService,
    private val stravaRepository: StravaRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {
    
    private val fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
    private val _lastKnownLocation = MutableStateFlow<LatLng?>(null)
    val lastKnownLocation: StateFlow<LatLng?> = _lastKnownLocation.asStateFlow()

    init {
        fetchLastLocation()
    }

    private fun fetchLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    _lastKnownLocation.value = LatLng(it.latitude, it.longitude)
                }
            }
        } catch (e: SecurityException) {
            // Handle permission error
        }
    }

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<Advertisement>>(emptyList())
    
    val synchronizedDevices = deviceRepository.getAllSynchronizedDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val discoveredDevices: StateFlow<List<Advertisement>> = _discoveredDevices
        .combine(synchronizedDevices) { discovered, synchronized ->
            discovered.filter { adv -> 
                synchronized.none { it.macAddress == adv.identifier }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sensorData = bleManager.sensorData
    val connectionStates = bleManager.connectionStates
    val deviceCapabilities = bleManager.deviceCapabilities

    private val _infoMessage = MutableSharedFlow<String>()
    val infoMessage = _infoMessage.asSharedFlow()

    val currentMapStyle = settingsRepository.mapStyleFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MapStyle.CLASSIC)

    fun setMapStyle(style: MapStyle) {
        viewModelScope.launch {
            settingsRepository.setMapStyle(style)
        }
    }

    val maxHeartRate = settingsRepository.maxHeartRateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 190)

    fun setMaxHeartRate(maxHr: Int) {
        viewModelScope.launch {
            settingsRepository.setMaxHeartRate(maxHr)
        }
    }

    val hrCalculationMode = settingsRepository.hrCalculationModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HrCalculationMode.FCM)

    fun setHrCalculationMode(mode: HrCalculationMode) {
        viewModelScope.launch {
            settingsRepository.setHrCalculationMode(mode)
        }
    }

    val restingHeartRate = settingsRepository.restingHeartRateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 60)

    fun setRestingHeartRate(hr: Int) {
        viewModelScope.launch {
            settingsRepository.setRestingHeartRate(hr)
        }
    }

    val lactateThreshold = settingsRepository.lactateThresholdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 160)

    fun setLactateThreshold(threshold: Int) {
        viewModelScope.launch {
            settingsRepository.setLactateThreshold(threshold)
        }
    }

    val zoneBoundaries = combine(
        hrCalculationMode,
        maxHeartRate,
        restingHeartRate,
        lactateThreshold
    ) { mode, maxHr, restHr, thresholdHr ->
        heartRateZoneService.getZoneBoundaries(mode, maxHr, restHr, thresholdHr)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val offlineRegions = offlineMapManager.regions
    val downloadStates = offlineMapManager.downloadStates

    fun downloadRegion(name: String, bounds: LatLngBounds) {
        val style = currentMapStyle.value
        val isSatellite = style == MapStyle.SATELLITE
        offlineMapManager.downloadRegion(name, bounds, style.url, isSatellite)
    }

    fun deleteOfflineRegion(region: org.maplibre.android.offline.OfflineRegion) {
        offlineMapManager.deleteRegion(region)
    }

    fun getRegionName(region: org.maplibre.android.offline.OfflineRegion): String {
        return offlineMapManager.getRegionName(region)
    }

    private var scanJob: Job? = null

    fun startScan() {
        if (_isScanning.value) return
        _isScanning.value = true
        _discoveredDevices.value = emptyList()

        scanJob = viewModelScope.launch {
            bleManager.scanDevices(listOf(
                SensorType.HEART_RATE, 
                SensorType.SPEED, 
                SensorType.CADENCE, 
                SensorType.CADENCE_SPEED, 
                SensorType.POWER
            ))
                .onCompletion { _isScanning.value = false }
                .collect { advertisement ->
                    if (_discoveredDevices.value.none { it.identifier == advertisement.identifier }) {
                        _discoveredDevices.update { it + advertisement }
                    }
                }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        _isScanning.value = false
    }

    fun syncDevice(advertisement: Advertisement, type: SensorType) {
        viewModelScope.launch {
            try {
                deviceRepository.saveDevice(
                    SynchronizedDeviceEntity(
                        macAddress = advertisement.identifier,
                        name = advertisement.name ?: "Appareil Inconnu",
                        type = type.name
                    )
                )
                _infoMessage.emit("Appareil synchronisé avec succès")
            } catch (e: Exception) {
                _infoMessage.emit("Erreur lors de la synchronisation")
            }
        }
    }

    fun unsyncDevice(device: SynchronizedDeviceEntity) {
        viewModelScope.launch {
            bleManager.disconnectDevice(device.macAddress)
            deviceRepository.deleteDevice(device)
            _infoMessage.emit("Appareil supprimé")
        }
    }

    fun connectDevice(address: String, type: SensorType) {
        bleManager.connectToDevice(address, type)
    }

    fun disconnectDevice(address: String) {
        bleManager.disconnectDevice(address)
    }

    val isStravaConnected = stravaRepository.isConnected
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun disconnectStrava() {
        viewModelScope.launch {
            stravaRepository.disconnect()
            _infoMessage.emit("Déconnecté de Strava")
        }
    }

    // --- Suivi live public ---

    val liveTrackingEnabled = settingsRepository.liveTrackingEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val liveTrackingBaseUrl = settingsRepository.liveTrackingBaseUrlFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val liveTrackingIngestKey = settingsRepository.liveTrackingIngestKeyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val lastShareUrl = settingsRepository.lastShareUrlFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setLiveTrackingEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setLiveTrackingEnabled(enabled) }
    }

    fun setLiveTrackingBaseUrl(url: String) {
        viewModelScope.launch { settingsRepository.setLiveTrackingBaseUrl(url) }
    }

    fun setLiveTrackingIngestKey(key: String) {
        viewModelScope.launch { settingsRepository.setLiveTrackingIngestKey(key) }
    }
}
