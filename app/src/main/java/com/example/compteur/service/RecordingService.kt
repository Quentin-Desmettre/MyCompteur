package com.example.compteur.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.compteur.CompteurApplication.Companion.CHANNEL_ID
import com.example.compteur.MainActivity
import com.example.compteur.R
import com.example.compteur.data.db.dao.GpsPointDao
import com.example.compteur.data.db.dao.SensorDataDao
import com.example.compteur.data.db.entity.GpsPointEntity
import com.example.compteur.data.db.entity.SensorDataEntity
import com.example.compteur.data.api.GeoPointDto
import com.example.compteur.data.api.StartSessionDto
import com.example.compteur.data.repository.SettingsRepository
import com.example.compteur.domain.model.RoutePoint
import com.example.compteur.domain.repository.DeviceRepository
import com.example.compteur.domain.repository.LiveTrackingRepository
import com.example.compteur.domain.repository.RouteRepository
import com.example.compteur.utils.RouteIndex
import com.example.compteur.utils.RouteMath
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@AndroidEntryPoint
class RecordingService : Service() {

    @Inject lateinit var gpsDao: GpsPointDao
    @Inject lateinit var sensorDataDao: SensorDataDao
    @Inject lateinit var bleManager: BleManager
    @Inject lateinit var deviceRepository: DeviceRepository
    @Inject lateinit var sessionRepository: com.example.compteur.domain.repository.SessionRepository
    @Inject lateinit var routeRepository: RouteRepository
    @Inject lateinit var liveTrackingRepository: LiveTrackingRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var appScope: CoroutineScope

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var batchWriter: BatchWriter? = null
    private var liveTracker: LiveTracker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _currentLocation = MutableStateFlow<android.location.Location?>(null)
    val currentLocation: StateFlow<android.location.Location?> = _currentLocation.asStateFlow()

    private val _recordedPath = MutableStateFlow<List<android.location.Location>>(emptyList())
    val recordedPath: StateFlow<List<android.location.Location>> = _recordedPath.asStateFlow()

    private val _totalDistanceMeters = MutableStateFlow(0f)
    val totalDistanceMeters: StateFlow<Float> = _totalDistanceMeters.asStateFlow()

    private val _totalAscentMeters = MutableStateFlow(0f)
    val totalAscentMeters: StateFlow<Float> = _totalAscentMeters.asStateFlow()

    private val _elapsedTimeSeconds = MutableStateFlow(0L)
    val elapsedTimeSeconds: StateFlow<Long> = _elapsedTimeSeconds.asStateFlow()

    private var lastLocation: android.location.Location? = null
    private var currentSessionId: Long = -1L
    private var timerJob: Job? = null
    private var notificationUpdateJob: Job? = null
    private var bleObserveJob: Job? = null

    inner class LocalBinder : Binder() {
        fun getService(): RecordingService = this@RecordingService
    }

    override fun onBind(intent: Intent?): IBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START -> startRecording(it.getLongExtra(EXTRA_SESSION_ID, -1L))
                ACTION_STOP -> stopRecording()
                ACTION_PAUSE -> pauseRecording()
                ACTION_RESUME -> resumeRecording()
            }
        }
        return START_STICKY
    }

    private fun startRecording(sessionId: Long) {
        if (sessionId == -1L || _isRecording.value) return

        val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        
        if (!hasFine && !hasCoarse) {
            stopSelf()
            return
        }

        currentSessionId = sessionId
        _isRecording.value = true
        _isPaused.value = false
        _totalDistanceMeters.value = 0f
        _totalAscentMeters.value = 0f
        _recordedPath.value = emptyList()
        _elapsedTimeSeconds.value = 0L
        lastLocation = null
        
        batchWriter = BatchWriter(gpsDao, sensorDataDao, serviceScope, sessionId)

        // Suivi live public (optionnel, faible conso) : démarré en parallèle, n'impacte pas le GPS.
        serviceScope.launch {
            try {
                if (settingsRepository.liveTrackingEnabledFlow.first()) {
                    startLiveTracking(sessionId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        startForeground(NOTIFICATION_ID, createNotification(), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        requestLocationUpdates()
        observeBleData()
        startTimer()
        startNotificationUpdates()
        
        // Auto-connect to synchronized devices
        serviceScope.launch {
            deviceRepository.getAllSynchronizedDevices().first().forEach { device ->
                bleManager.connectToDevice(device.macAddress, SensorType.valueOf(device.type))
            }
        }
    }

    private suspend fun startLiveTracking(sessionId: Long) {
        val baseUrl = settingsRepository.liveTrackingBaseUrlFlow.first().trim().trimEnd('/')
        if (baseUrl.isBlank()) return

        val routeId = sessionRepository.getSessionById(sessionId)?.routeId
        val token = java.util.UUID.randomUUID().toString().replace("-", "").take(16)

        var routeIndex: RouteIndex? = null
        var routePoints: List<GeoPointDto> = emptyList()
        var routeName: String? = null
        var totalRoute = 0.0
        if (routeId != null) {
            val pts = routeRepository.getPointsForRoute(routeId).first()
            if (pts.isNotEmpty()) {
                routeIndex = RouteMath.buildIndex(pts.map { it.latitude to it.longitude })
                totalRoute = routeIndex.totalMeters
                routePoints = downsample(pts).map { GeoPointDto(it.latitude, it.longitude, it.altitudeMeters) }
                routeName = routeRepository.getRouteById(routeId)?.name
            }
        }

        val tracker = LiveTracker(
            repo = liveTrackingRepository,
            scope = appScope,
            shareToken = token,
            routeIndex = routeIndex,
            snapshotProvider = { snapshot() }
        )
        liveTracker = tracker
        tracker.start(
            StartSessionDto(
                shareToken = token,
                athleteName = null,
                routeName = routeName,
                totalRouteMeters = totalRoute,
                routePoints = routePoints,
                startedAt = System.currentTimeMillis()
            )
        )
        settingsRepository.setLastShareUrl("$baseUrl/live/$token")
    }

    private fun snapshot(): LiveSnapshot {
        val loc = _currentLocation.value
        val ble = bleManager.sensorData.value
        return LiveSnapshot(
            lat = loc?.latitude,
            lng = loc?.longitude,
            speedKph = ble.speedKph,
            hr = ble.heartRate,
            cadence = ble.cadence,
            power = ble.power,
            ascentM = _totalAscentMeters.value,
            distTotalM = _totalDistanceMeters.value,
            elapsedS = _elapsedTimeSeconds.value,
            gpsSpeedMps = loc?.speed
        )
    }

    /** Réduit la géométrie envoyée une seule fois au démarrage (cap ~1500 points) pour limiter le payload. */
    private fun downsample(pts: List<RoutePoint>, max: Int = 1500): List<RoutePoint> {
        if (pts.size <= max) return pts
        val step = pts.size.toDouble() / max
        val out = ArrayList<RoutePoint>(max + 1)
        var i = 0.0
        while (i < pts.size) {
            out.add(pts[i.toInt()])
            i += step
        }
        if (out.isEmpty() || out.last() !== pts.last()) out.add(pts.last())
        return out
    }

    private fun pauseRecording() {
        if (!_isRecording.value || _isPaused.value) return
        _isPaused.value = true
        fusedLocationClient.removeLocationUpdates(locationCallback)
        timerJob?.cancel()
        updateNotification()
    }

    private fun resumeRecording() {
        if (!_isRecording.value || !_isPaused.value) return
        _isPaused.value = false
        requestLocationUpdates()
        startTimer()
        updateNotification()
    }

    private fun stopRecording() {
        val finalDistance = _totalDistanceMeters.value
        val finalAscent = _totalAscentMeters.value
        val sessionId = currentSessionId
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val session = sessionRepository.getSessionById(sessionId)
                if (session != null) {
                    val durationMs = System.currentTimeMillis() - session.startedAt
                    val durationHours = durationMs / 3600000.0
                    val avgSpeedKph = if (durationHours > 0) (finalDistance / 1000.0) / durationHours else 0.0

                    val updated = session.copy(
                        endedAt = System.currentTimeMillis(),
                        distanceMeters = finalDistance,
                        ascentMeters = finalAscent,
                        avgSpeedKph = avgSpeedKph.toFloat()
                    )
                    sessionRepository.saveSession(updated)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        _isRecording.value = false
        _isPaused.value = false
        timerJob?.cancel()
        notificationUpdateJob?.cancel()
        bleObserveJob?.cancel()
        batchWriter?.stop()
        liveTracker?.stop()
        liveTracker = null
        fusedLocationClient.removeLocationUpdates(locationCallback)
        bleManager.disconnectAll()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive) {
                delay(1000L)
                if (!_isPaused.value) {
                    _elapsedTimeSeconds.value += 1
                }
            }
        }
    }

    private fun startNotificationUpdates() {
        notificationUpdateJob?.cancel()
        notificationUpdateJob = serviceScope.launch {
            while (isActive) {
                delay(5000L) // Update notification every 5 seconds
                if (_isRecording.value) {
                    updateNotification()
                }
            }
        }
    }

    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(android.app.NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun requestLocationUpdates() {
        val interval = if (bleManager.sensorData.value.speedKph != null) 5000L else 1000L
        val priority = if (bleManager.sensorData.value.speedKph != null) Priority.PRIORITY_BALANCED_POWER_ACCURACY else Priority.PRIORITY_HIGH_ACCURACY

        val locationRequest = LocationRequest.Builder(priority, interval)
            .setMinUpdateIntervalMillis(interval)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            // Gérer les permissions dans l'UI
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                val prevLocation = lastLocation
                if (prevLocation != null && _isRecording.value && !_isPaused.value) {
                    val distance = prevLocation.distanceTo(location)
                    if (distance > 1.0) { // filter noise
                        _totalDistanceMeters.value += distance
                    }
                    if (location.hasAltitude() && prevLocation.hasAltitude()) {
                        val diff = location.altitude - prevLocation.altitude
                        if (diff > 0.5) { // simple noise filter
                            _totalAscentMeters.value += diff.toFloat()
                        }
                    }
                }
                lastLocation = location
                _currentLocation.value = location
                if (_isRecording.value && !_isPaused.value) {
                    _recordedPath.value = _recordedPath.value + location
                    batchWriter?.addGpsPoint(
                        GpsPointEntity(
                            sessionId = currentSessionId,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            altitudeMeters = location.altitude.toFloat(),
                            speedMps = location.speed,
                            timestampMs = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    private fun observeBleData() {
        bleObserveJob?.cancel()
        bleObserveJob = serviceScope.launch {
            bleManager.sensorData.collect { data ->
                if (_isRecording.value && !_isPaused.value) {
                    batchWriter?.addSensorData(
                        SensorDataEntity(
                            sessionId = currentSessionId,
                            timestampMs = System.currentTimeMillis(),
                            powerWatts = data.power,
                            cadenceRpm = data.cadence,
                            heartRateBpm = data.heartRate,
                            bleSpeedKph = data.speedKph
                        )
                    )
                }
            }
        }
    }

    private fun formatElapsedTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )

        val timeStr = formatElapsedTime(_elapsedTimeSeconds.value)
        val distKm = _totalDistanceMeters.value / 1000f
        val distStr = "%.1f km".format(distKm)
        
        val statusText = if (_isPaused.value) {
            "⏸ En pause — $timeStr • $distStr"
        } else {
            "🔴 $timeStr • $distStr"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_recording_title))
            .setContentText(statusText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val EXTRA_SESSION_ID = "EXTRA_SESSION_ID"
    }
}
