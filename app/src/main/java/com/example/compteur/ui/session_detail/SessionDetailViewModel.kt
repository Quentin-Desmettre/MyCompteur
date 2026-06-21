package com.example.compteur.ui.session_detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compteur.domain.model.RoutePoint
import com.example.compteur.domain.model.Session
import com.example.compteur.domain.repository.SessionRepository
import com.example.compteur.domain.repository.StravaRepository
import com.example.compteur.utils.FitExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionRepository: SessionRepository,
    private val stravaRepository: StravaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session.asStateFlow()

    private val _routePoints = MutableStateFlow<List<RoutePoint>>(emptyList())
    val routePoints: StateFlow<List<RoutePoint>> = _routePoints.asStateFlow()

    init {
        loadSession()
        loadRoutePoints()
    }

    private fun loadSession() {
        viewModelScope.launch {
            _session.value = sessionRepository.getSessionById(sessionId)
        }
    }

    private fun loadRoutePoints() {
        viewModelScope.launch {
            val entities = sessionRepository.getGpsPointsForSession(sessionId).first()
            _routePoints.value = entities.map {
                RoutePoint(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    altitudeMeters = it.altitudeMeters
                )
            }
        }
    }

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    val isStravaConnected = stravaRepository.isConnected
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), false)

    fun exportForDownload() {
        exportSession(isStrava = false)
    }

    fun exportForStrava(
        name: String = "Activité Vélo",
        description: String = "Enregistré avec MyCompteur",
        isCommute: Boolean = false,
        isTrainer: Boolean = false,
        isPrivate: Boolean = false
    ) {
        exportSession(
            isStrava = true,
            stravaName = name,
            stravaDescription = description,
            isCommute = isCommute,
            isTrainer = isTrainer,
            isPrivate = isPrivate
        )
    }

    private fun exportSession(
        isStrava: Boolean,
        stravaName: String = "",
        stravaDescription: String = "",
        isCommute: Boolean = false,
        isTrainer: Boolean = false,
        isPrivate: Boolean = false
    ) {
        val currentSession = _session.value ?: return
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            try {
                val gpsPoints = sessionRepository.getGpsPointsForSession(currentSession.id).first()
                val sensorData = sessionRepository.getSensorDataForSession(currentSession.id).first()

                val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
                val dateString = formatter.format(Date(currentSession.startedAt))
                val fileName = "Activity_${dateString}.fit"
                
                val exportDir = File(context.cacheDir, "exports")
                if (!exportDir.exists()) exportDir.mkdirs()
                val file = File(exportDir, fileName)

                FitExporter.exportSession(currentSession, gpsPoints, sensorData, file)

                if (isStrava) {
                    if (isStravaConnected.value) {
                        val result = stravaRepository.uploadActivity(
                            fitFile = file,
                            name = stravaName,
                            description = stravaDescription,
                            isCommute = isCommute,
                            isTrainer = isTrainer,
                            isPrivate = isPrivate
                        )
                        if (result.isSuccess) {
                            _exportState.value = ExportState.SuccessStravaUpload
                        } else {
                            _exportState.value = ExportState.Error("Erreur d'upload: ${result.exceptionOrNull()?.message} ${result.exceptionOrNull()}")
                        }
                    } else {
                        _exportState.value = ExportState.RequireStravaLogin
                    }
                } else {
                    _exportState.value = ExportState.SuccessDownload(file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _exportState.value = ExportState.Error("Erreur: ${e.message}")
            }
        }
    }

    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }
}

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class SuccessDownload(val file: File) : ExportState()
    object RequireStravaLogin : ExportState()
    object SuccessStravaUpload : ExportState()
    data class Error(val message: String) : ExportState()
}
