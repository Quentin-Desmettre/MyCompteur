package com.example.compteur.domain.model

data class Route(
    val id: Long,
    val name: String,
    val distanceMeters: Float,
    val ascentMeters: Float,
    val dateImported: Long
)

data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Float?
)

data class Session(
    val id: Long,
    val routeId: Long?,
    val startedAt: Long,
    val endedAt: Long?,
    val distanceMeters: Float,
    val avgSpeedKph: Float,
    val maxSpeedKph: Float,
    val avgPowerWatts: Int?,
    val avgHeartRateBpm: Int?,
    val ascentMeters: Float
)
