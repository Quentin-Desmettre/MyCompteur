package com.example.compteur.ui.recording

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compteur.domain.model.RoutePoint
import com.example.compteur.domain.usecase.GetRoutePointsUseCase
import com.example.compteur.domain.usecase.GetRoutesUseCase
import com.example.compteur.domain.usecase.GetSessionsUseCase
import com.example.compteur.domain.usecase.StartSessionUseCase
import com.example.compteur.service.BleManager
import com.example.compteur.service.BleSensorData
import com.example.compteur.service.RecordingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.compteur.data.repository.SettingsRepository
import com.example.compteur.data.repository.MapStyle
import com.example.compteur.data.repository.HrCalculationMode
import com.example.compteur.service.HeartRateZoneService
import com.example.compteur.service.HeartRateZoneInfo

@HiltViewModel
class RecordingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val startSessionUseCase: StartSessionUseCase,
    private val getRoutesUseCase: GetRoutesUseCase,
    private val getRoutePointsUseCase: GetRoutePointsUseCase,
    private val settingsRepository: SettingsRepository,
    private val bleManager: BleManager,
    private val heartRateZoneService: HeartRateZoneService
) : ViewModel() {

    private val targetRouteId: Long = savedStateHandle["routeId"] ?: -1L

    val plannedPoints: StateFlow<List<RoutePoint>> = if (targetRouteId != -1L) {
        getRoutePointsUseCase(targetRouteId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } else {
        MutableStateFlow(emptyList())
    }

    val mapStyleUrl: StateFlow<String> = settingsRepository.mapStyleFlow
        .map { it.url }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MapStyle.CLASSIC.url)

    val maxHeartRate: StateFlow<Int> = settingsRepository.maxHeartRateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 190)

    val hrCalculationMode: StateFlow<HrCalculationMode> = settingsRepository.hrCalculationModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HrCalculationMode.FCM)

    val restingHeartRate: StateFlow<Int> = settingsRepository.restingHeartRateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 60)

    val lactateThreshold: StateFlow<Int> = settingsRepository.lactateThresholdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 160)

    private var recordingService: RecordingService? = null
    private val _isServiceBound = MutableStateFlow(false)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RecordingService.LocalBinder
            recordingService = binder.getService()
            _isServiceBound.value = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            recordingService = null
            _isServiceBound.value = false
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isRecording: StateFlow<Boolean> = _isServiceBound.flatMapLatest { bound ->
        if (bound) recordingService?.isRecording ?: flowOf(false) else flowOf(false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val isPaused: StateFlow<Boolean> = _isServiceBound.flatMapLatest { bound ->
        if (bound) recordingService?.isPaused ?: flowOf(false) else flowOf(false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentLocation = _isServiceBound.flatMapLatest { bound ->
        if (bound) recordingService?.currentLocation ?: flowOf(null) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val recordedPath = _isServiceBound.flatMapLatest { bound ->
        if (bound) recordingService?.recordedPath ?: flowOf(emptyList()) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalDistance = _isServiceBound.flatMapLatest { bound ->
        if (bound) recordingService?.totalDistanceMeters ?: flowOf(0f) else flowOf(0f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    @OptIn(ExperimentalCoroutinesApi::class)
    val elapsedTimeSeconds: StateFlow<Long> = _isServiceBound.flatMapLatest { bound ->
        if (bound) recordingService?.elapsedTimeSeconds ?: flowOf(0L) else flowOf(0L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Sensor data from BLE manager (available even before recording)
    val sensorData: StateFlow<BleSensorData> = bleManager.sensorData

    val heartRateZoneInfo: StateFlow<HeartRateZoneInfo?> = combine(
        bleManager.sensorData,
        hrCalculationMode,
        maxHeartRate,
        restingHeartRate,
        lactateThreshold
    ) { sensor, mode, maxHr, restHr, thresholdHr ->
        val hr = sensor.heartRate
        if (hr != null) {
            heartRateZoneService.calculateZone(hr, mode, maxHr, restHr, thresholdHr)
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Display speed: BLE sensor if available, otherwise GPS
    val displaySpeedKph: StateFlow<Float> = combine(
        bleManager.sensorData,
        currentLocation
    ) { sensor, location ->
        sensor.speedKph ?: (location?.speed?.times(3.6f) ?: 0f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // Display distance
    val displayDistanceKm: StateFlow<Float> = totalDistance
        .map { it / 1000f }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    init {
        val intent = Intent(context, RecordingService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun startRecording() {
        viewModelScope.launch {
            val service = recordingService ?: return@launch
            if (!service.isRecording.value) {
                val sessionId = startSessionUseCase(if (targetRouteId != -1L) targetRouteId else null)
                val intent = Intent(context, RecordingService::class.java).apply {
                    action = RecordingService.ACTION_START
                    putExtra(RecordingService.EXTRA_SESSION_ID, sessionId)
                }
                context.startForegroundService(intent)
            }
        }
    }

    fun stopRecording() {
        val intent = Intent(context, RecordingService::class.java).apply {
            action = RecordingService.ACTION_STOP
        }
        context.startForegroundService(intent)
    }

    fun pauseRecording() {
        val intent = Intent(context, RecordingService::class.java).apply {
            action = RecordingService.ACTION_PAUSE
        }
        context.startForegroundService(intent)
    }

    fun resumeRecording() {
        val intent = Intent(context, RecordingService::class.java).apply {
            action = RecordingService.ACTION_RESUME
        }
        context.startForegroundService(intent)
    }

    fun togglePause() {
        if (isPaused.value) resumeRecording() else pauseRecording()
    }

    override fun onCleared() {
        super.onCleared()
        context.unbindService(serviceConnection)
    }
}
