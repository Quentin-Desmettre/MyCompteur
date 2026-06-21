package com.example.compteur.service

import androidx.compose.ui.graphics.Color
import com.example.compteur.data.repository.HrCalculationMode
import javax.inject.Inject
import javax.inject.Singleton

data class HeartRateZoneInfo(
    val bgColor: Color,
    val nextColor: Color?,
    val progress: Float?
)

data class ZoneBoundary(val name: String, val minBpm: Int, val maxBpm: Int, val color: Color)

@Singleton
class HeartRateZoneService @Inject constructor() {

    fun calculateZone(
        currentHr: Int,
        mode: HrCalculationMode,
        maxHr: Int,
        restHr: Int,
        thresholdHr: Int
    ): HeartRateZoneInfo {
        return when (mode) {
            HrCalculationMode.FCM -> calculateFcm(currentHr, maxHr)
            HrCalculationMode.KARVONEN -> calculateKarvonen(currentHr, maxHr, restHr)
            HrCalculationMode.LACTATE_THRESHOLD -> calculateLactateThreshold(currentHr, thresholdHr)
        }
    }

    fun getZoneBoundaries(
        mode: HrCalculationMode,
        maxHr: Int,
        restHr: Int,
        thresholdHr: Int
    ): List<ZoneBoundary> {
        val maxBpmFallback = 250
        return when (mode) {
            HrCalculationMode.FCM -> listOf(
                ZoneBoundary("Z1 (Récupération)", 0, (maxHr * 0.60f).toInt(), Color(0xFF43A047)),
                ZoneBoundary("Z2 (Endurance)", (maxHr * 0.60f).toInt(), (maxHr * 0.70f).toInt(), Color(0xFF1E88E5)),
                ZoneBoundary("Z3 (Tempo)", (maxHr * 0.70f).toInt(), (maxHr * 0.80f).toInt(), Color(0xFFFFEB3B)),
                ZoneBoundary("Z4 (Seuil)", (maxHr * 0.80f).toInt(), (maxHr * 0.90f).toInt(), Color(0xFFFB8C00)),
                ZoneBoundary("Z5 (Maximale)", (maxHr * 0.90f).toInt(), maxHr, Color(0xFFE53935))
            )
            HrCalculationMode.KARVONEN -> {
                val reserve = maxHr - restHr
                listOf(
                    ZoneBoundary("Z1 (Récupération)", 0, restHr + (reserve * 0.60f).toInt(), Color(0xFF43A047)),
                    ZoneBoundary("Z2 (Endurance)", restHr + (reserve * 0.60f).toInt(), restHr + (reserve * 0.70f).toInt(), Color(0xFF1E88E5)),
                    ZoneBoundary("Z3 (Tempo)", restHr + (reserve * 0.70f).toInt(), restHr + (reserve * 0.80f).toInt(), Color(0xFFFFEB3B)),
                    ZoneBoundary("Z4 (Seuil)", restHr + (reserve * 0.80f).toInt(), restHr + (reserve * 0.90f).toInt(), Color(0xFFFB8C00)),
                    ZoneBoundary("Z5 (Maximale)", restHr + (reserve * 0.90f).toInt(), maxHr, Color(0xFFE53935))
                )
            }
            HrCalculationMode.LACTATE_THRESHOLD -> listOf(
                ZoneBoundary("Z1 (Récupération)", 0, (thresholdHr * 0.85f).toInt(), Color(0xFF43A047)),
                ZoneBoundary("Z2 (Endurance)", (thresholdHr * 0.85f).toInt(), (thresholdHr * 0.89f).toInt(), Color(0xFF1E88E5)),
                ZoneBoundary("Z3 (Tempo)", (thresholdHr * 0.89f).toInt(), (thresholdHr * 0.94f).toInt(), Color(0xFFFFEB3B)),
                ZoneBoundary("Z4 (Seuil)", (thresholdHr * 0.94f).toInt(), (thresholdHr * 0.99f).toInt(), Color(0xFFFB8C00)),
                ZoneBoundary("Z5 (Maximale)", (thresholdHr * 0.99f).toInt(), maxBpmFallback, Color(0xFFE53935))
            )
        }
    }

    private fun calculateFcm(currentHr: Int, maxHr: Int): HeartRateZoneInfo {
        val ratio = currentHr.toFloat() / maxHr.toFloat()
        return getZoneInfoFromRatio(
            ratio = ratio,
            z1Max = 0.60f,
            z2Max = 0.70f,
            z3Max = 0.80f,
            z4Max = 0.90f
        )
    }

    private fun calculateKarvonen(currentHr: Int, maxHr: Int, restHr: Int): HeartRateZoneInfo {
        val reserve = maxHr - restHr
        val ratio = if (reserve > 0) {
            (currentHr - restHr).toFloat() / reserve.toFloat()
        } else {
            0f
        }
        // Zones de Karvonen classiques :
        // Z1: 50-60%, Z2: 60-70%, Z3: 70-80%, Z4: 80-90%, Z5: 90-100%
        return getZoneInfoFromRatio(
            ratio = ratio,
            z1Max = 0.60f,
            z2Max = 0.70f,
            z3Max = 0.80f,
            z4Max = 0.90f
        )
    }

    private fun calculateLactateThreshold(currentHr: Int, thresholdHr: Int): HeartRateZoneInfo {
        val ratio = currentHr.toFloat() / thresholdHr.toFloat()
        // Zones classiques par rapport au seuil lactique :
        // Z1: < 85%
        // Z2: 85% - 89%
        // Z3: 90% - 94%
        // Z4: 95% - 99%
        // Z5: >= 100%
        return getZoneInfoFromRatio(
            ratio = ratio,
            z1Max = 0.85f,
            z2Max = 0.89f,
            z3Max = 0.94f,
            z4Max = 0.99f
        )
    }

    private fun getZoneInfoFromRatio(
        ratio: Float,
        z1Max: Float,
        z2Max: Float,
        z3Max: Float,
        z4Max: Float
    ): HeartRateZoneInfo {
        val currentZoneMin: Float
        val currentZoneMax: Float
        val bgColor: Color
        val nextColor: Color?

        when {
            ratio < z1Max -> {
                currentZoneMin = 0f
                currentZoneMax = z1Max
                bgColor = Color(0xFF43A047) // Green
                nextColor = Color(0xFF1E88E5) // Blue
            }
            ratio < z2Max -> {
                currentZoneMin = z1Max
                currentZoneMax = z2Max
                bgColor = Color(0xFF1E88E5) // Blue
                nextColor = Color(0xFFFFEB3B) // Yellow
            }
            ratio < z3Max -> {
                currentZoneMin = z2Max
                currentZoneMax = z3Max
                bgColor = Color(0xFFFFEB3B) // Yellow
                nextColor = Color(0xFFFB8C00) // Orange
            }
            ratio < z4Max -> {
                currentZoneMin = z3Max
                currentZoneMax = z4Max
                bgColor = Color(0xFFFB8C00) // Orange
                nextColor = Color(0xFFE53935) // Red
            }
            else -> {
                currentZoneMin = z4Max
                currentZoneMax = 1.0f // Not strictly used for progress if nextColor is null
                bgColor = Color(0xFFE53935) // Red
                nextColor = null
            }
        }

        val progress = if (nextColor != null) {
            (ratio - currentZoneMin) / (currentZoneMax - currentZoneMin)
        } else null

        return HeartRateZoneInfo(
            bgColor = bgColor,
            nextColor = nextColor,
            progress = progress?.coerceIn(0f, 1f)
        )
    }
}
