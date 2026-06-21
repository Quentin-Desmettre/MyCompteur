package com.example.compteur.ui.route_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compteur.domain.model.Route
import com.example.compteur.domain.model.RoutePoint
import com.example.compteur.domain.usecase.GetRoutePointsUseCase
import com.example.compteur.domain.usecase.GetRouteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRouteUseCase: GetRouteUseCase,
    private val getRoutePointsUseCase: GetRoutePointsUseCase
) : ViewModel() {

    private val routeId: Long = checkNotNull(savedStateHandle["routeId"])

    private val _route = MutableStateFlow<Route?>(null)
    val route: StateFlow<Route?> = _route.asStateFlow()

    val routePoints: StateFlow<List<RoutePoint>> = getRoutePointsUseCase(routeId)
        .onEach { android.util.Log.d("RouteDetailViewModel", "Emitting ${it.size} points to UI") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _route.value = getRouteUseCase(routeId)
        }
    }
}
