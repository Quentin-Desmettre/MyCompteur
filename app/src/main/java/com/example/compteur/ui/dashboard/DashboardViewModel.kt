package com.example.compteur.ui.dashboard

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compteur.domain.model.Route
import com.example.compteur.domain.usecase.DeleteRouteUseCase
import com.example.compteur.domain.usecase.GetRoutePointsUseCase
import com.example.compteur.domain.usecase.GetRoutesUseCase
import com.example.compteur.domain.usecase.ImportGpxUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.Console
import javax.inject.Inject

import com.example.compteur.data.repository.SettingsRepository
import com.example.compteur.data.repository.MapStyle

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getRoutesUseCase: GetRoutesUseCase,
    private val importGpxUseCase: ImportGpxUseCase,
    private val deleteRouteUseCase: DeleteRouteUseCase,
    private val getRoutePointsUseCase: GetRoutePointsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val routes: StateFlow<List<Route>> = getRoutesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mapStyleUrl: StateFlow<String> = settingsRepository.mapStyleFlow
        .map { it.url }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MapStyle.CLASSIC.url)

    private val _selectedRouteId = MutableStateFlow<Long?>(null)
    val selectedRouteId: StateFlow<Long?> = _selectedRouteId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedRoutePoints = _selectedRouteId.flatMapLatest { id ->
        if (id != null) getRoutePointsUseCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorMessage: StateFlow<Int?> = _errorMessage

    fun selectRoute(id: Long) {
        _selectedRouteId.value = id
    }

    fun importGpx(context: Context, uri: Uri) {
        println("starting import gpx")
        viewModelScope.launch {
            try {
                println("starting function")
                val contentResolver = context.contentResolver
                val fileName = uri.path?.substringAfterLast("/") ?: "Route"
                // log fileName
                println("fileName: $fileName")
                
                // Vérification simple de l'extension si possible
                if (!fileName.lowercase().endsWith(".gpx") && !fileName.lowercase().endsWith(".xml")) {
                    // On peut quand même essayer de parser, mais c'est un bon indicateur
                }

                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use {
                    val result = importGpxUseCase(it, fileName)
                    if (result.isFailure) {
                        // log error
                        println("error: ${result.exceptionOrNull()}")
                        _errorMessage.value = com.example.compteur.R.string.error_invalid_gpx
                    } else {
                        println("successfully imported gpx")
                    }
                } ?: run {
                    // log error
                    println("error: inputStream is null")
                    _errorMessage.value = com.example.compteur.R.string.error_import_failed
                }
            } catch (e: Exception) {
                // log error
                println("error: ${e.message}")
                _errorMessage.value = com.example.compteur.R.string.error_import_failed
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun deleteRoute(route: Route) {
        viewModelScope.launch {
            deleteRouteUseCase(route)
        }
    }
}
