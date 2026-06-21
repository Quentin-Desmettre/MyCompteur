package com.example.compteur.ui.history

import android.content.Context
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compteur.domain.model.Session
import com.example.compteur.domain.repository.SessionRepository
import com.example.compteur.utils.FitExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val sessions: StateFlow<List<Session>> = sessionRepository.getAllSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            sessionRepository.deleteSession(session)
        }
    }

    fun exportSessionToFit(session: Session) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            try {
                val gpsPoints = sessionRepository.getGpsPointsForSession(session.id).first()
                val sensorData = sessionRepository.getSensorDataForSession(session.id).first()

                val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
                val dateString = formatter.format(Date(session.startedAt))
                val fileName = "Activity_\${dateString}.fit"
                
                // Export to internal cache directory first
                val exportDir = File(context.cacheDir, "exports")
                if (!exportDir.exists()) exportDir.mkdirs()
                val file = File(exportDir, fileName)

                FitExporter.exportSession(session, gpsPoints, sensorData, file)

                _exportState.value = ExportState.Success(file)
            } catch (e: Exception) {
                e.printStackTrace()
                _exportState.value = ExportState.Error("Erreur lors de l'export: \${e.message}")
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
    data class Success(val file: File) : ExportState()
    data class Error(val message: String) : ExportState()
}
