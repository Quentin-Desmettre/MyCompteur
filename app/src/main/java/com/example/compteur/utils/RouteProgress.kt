package com.example.compteur.utils

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt

/** Résultat de la projection d'une position sur un parcours. */
data class RouteProgress(
    val distanceAlongMeters: Double,
    val distanceRemainingMeters: Double,
    val fractionDone: Double
)

/**
 * Index précalculé d'un parcours : coordonnées + distances cumulées depuis le départ.
 * Construit une seule fois au démarrage de l'enregistrement.
 */
class RouteIndex(
    val lats: DoubleArray,
    val lngs: DoubleArray,
    val cumulative: DoubleArray
) {
    val size: Int get() = lats.size
    val totalMeters: Double get() = if (cumulative.isEmpty()) 0.0 else cumulative[cumulative.size - 1]
}

/**
 * Maths de progression sur parcours, en Kotlin pur (testable en JVM, sans dépendance Android).
 * Haversine pour les distances, projection perpendiculaire en approximation équirectangulaire
 * locale pour situer la position sur la polyligne.
 */
object RouteMath {
    private const val EARTH_RADIUS_M = 6_371_000.0
    private const val DEG_TO_RAD = Math.PI / 180.0
    private const val METERS_PER_DEG_LAT = 111_320.0

    fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = (lat2 - lat1) * DEG_TO_RAD
        val dLon = (lon2 - lon1) * DEG_TO_RAD
        val a = sin(dLat / 2).pow(2) +
            cos(lat1 * DEG_TO_RAD) * cos(lat2 * DEG_TO_RAD) * sin(dLon / 2).pow(2)
        return 2 * EARTH_RADIUS_M * asin(min(1.0, sqrt(a)))
    }

    /** points = liste de (lat, lng). */
    fun buildIndex(points: List<Pair<Double, Double>>): RouteIndex {
        val n = points.size
        val lats = DoubleArray(n)
        val lngs = DoubleArray(n)
        val cum = DoubleArray(n)
        for (i in 0 until n) {
            lats[i] = points[i].first
            lngs[i] = points[i].second
            if (i > 0) {
                cum[i] = cum[i - 1] + haversineMeters(lats[i - 1], lngs[i - 1], lats[i], lngs[i])
            }
        }
        return RouteIndex(lats, lngs, cum)
    }

    /**
     * Projette (lat,lng) sur le parcours : trouve le segment le plus proche et renvoie la
     * distance parcourue le long du parcours, la distance restante et la fraction réalisée.
     */
    fun project(index: RouteIndex, lat: Double, lng: Double): RouteProgress {
        if (index.size == 0) return RouteProgress(0.0, 0.0, 0.0)
        val total = index.totalMeters
        if (index.size == 1 || total <= 0.0) return RouteProgress(0.0, total, 0.0)

        // Repère local en mètres centré sur la position interrogée (origine = (0,0)).
        val mPerDegLng = METERS_PER_DEG_LAT * cos(lat * DEG_TO_RAD)
        fun x(plng: Double) = (plng - lng) * mPerDegLng
        fun y(plat: Double) = (plat - lat) * METERS_PER_DEG_LAT

        var bestDist = Double.MAX_VALUE
        var bestAlong = 0.0
        for (i in 0 until index.size - 1) {
            val ax = x(index.lngs[i]); val ay = y(index.lats[i])
            val bx = x(index.lngs[i + 1]); val by = y(index.lats[i + 1])
            val dx = bx - ax; val dy = by - ay
            val segLenSq = dx * dx + dy * dy
            val t = if (segLenSq <= 0.0) 0.0
            else (((-ax) * dx + (-ay) * dy) / segLenSq).coerceIn(0.0, 1.0)
            val projX = ax + t * dx
            val projY = ay + t * dy
            val d = hypot(projX, projY)
            if (d < bestDist) {
                bestDist = d
                val segLen = index.cumulative[i + 1] - index.cumulative[i]
                bestAlong = index.cumulative[i] + t * segLen
            }
        }
        val along = bestAlong.coerceIn(0.0, total)
        val remaining = (total - along).coerceAtLeast(0.0)
        return RouteProgress(along, remaining, along / total)
    }

    /**
     * ETA depuis la distance restante et la vitesse (m/s). Renvoie (secondesRestantes, etaEpochMs).
     * Si la vitesse est trop faible pour être significative, renvoie (-1, -1).
     */
    fun etaSecondsAndEpoch(remainingMeters: Double, speedMps: Double, nowEpochMs: Long): Pair<Long, Long> {
        if (speedMps <= 0.1) return -1L to -1L
        val secs = (remainingMeters / speedMps).roundToLong()
        return secs to (nowEpochMs + secs * 1000L)
    }
}
