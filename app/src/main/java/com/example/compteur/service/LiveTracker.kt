package com.example.compteur.service

import com.example.compteur.data.api.IngestDto
import com.example.compteur.data.api.SampleDto
import com.example.compteur.data.api.StartSessionDto
import com.example.compteur.data.api.StopSessionDto
import com.example.compteur.domain.repository.LiveTrackingRepository
import com.example.compteur.utils.RouteIndex
import com.example.compteur.utils.RouteMath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

/** Instantané des compteurs au moment d'un envoi (lu depuis les StateFlow du service). */
data class LiveSnapshot(
    val lat: Double?,
    val lng: Double?,
    val speedKph: Float?,
    val hr: Int?,
    val cadence: Int?,
    val power: Int?,
    val ascentM: Float,
    val distTotalM: Float,
    val elapsedS: Long,
    val gpsSpeedMps: Float?
)

/**
 * Pousse la télémétrie live vers le backend toutes les [sendIntervalMs] (~15 s) pour minimiser
 * les réveils radio. Bufferise les échantillons hors-ligne et les renvoie groupés au retour réseau.
 *
 * Calqué sur [BatchWriter] : une seule coroutine périodique, travail réseau sur Dispatchers.IO.
 * Utilise le scope applicatif (et non celui du service) pour que le stopSession final survive à
 * l'arrêt du service.
 */
class LiveTracker(
    private val repo: LiveTrackingRepository,
    private val scope: CoroutineScope,
    private val shareToken: String,
    private val routeIndex: RouteIndex?,
    private val sendIntervalMs: Long = 15_000L,
    private val snapshotProvider: () -> LiveSnapshot
) {
    private val buffer = ArrayDeque<SampleDto>()
    private val maxBuffer = 200
    private var job: Job? = null

    // Fenêtre glissante de vitesse (60 s) pour lisser l'ETA.
    private val speedWindow = ArrayDeque<Pair<Long, Double>>()

    fun start(startBody: StartSessionDto) {
        job = scope.launch(Dispatchers.IO) {
            repo.startSession(startBody) // best-effort ; l'ingest réessaiera de toute façon
            while (isActive) {
                delay(sendIntervalMs)
                tick()
            }
        }
    }

    private suspend fun tick() {
        val s = snapshotProvider()
        val now = System.currentTimeMillis()

        val mps: Double? = when {
            s.speedKph != null -> s.speedKph / 3.6
            s.gpsSpeedMps != null -> s.gpsSpeedMps.toDouble()
            else -> null
        }
        if (mps != null) {
            speedWindow.addLast(now to mps)
            while (speedWindow.isNotEmpty() && now - speedWindow.first().first > 60_000L) {
                speedWindow.removeFirst()
            }
        }
        val avgMps = if (speedWindow.isNotEmpty()) speedWindow.sumOf { it.second } / speedWindow.size else 0.0

        var along: Double? = null
        var remaining: Double? = null
        var fraction: Double? = null
        var eta: Long? = null
        if (routeIndex != null && s.lat != null && s.lng != null) {
            val p = RouteMath.project(routeIndex, s.lat, s.lng)
            along = p.distanceAlongMeters
            remaining = p.distanceRemainingMeters
            fraction = p.fractionDone
            val (_, etaEpoch) = RouteMath.etaSecondsAndEpoch(p.distanceRemainingMeters, avgMps, now)
            if (etaEpoch > 0) eta = etaEpoch
        }

        val sample = SampleDto(
            t = now,
            lat = s.lat,
            lng = s.lng,
            speedKph = s.speedKph ?: s.gpsSpeedMps?.let { it * 3.6f },
            hr = s.hr,
            cadence = s.cadence,
            power = s.power,
            ascentM = s.ascentM,
            distTotalM = s.distTotalM,
            distAlongM = along,
            distRemainingM = remaining,
            fractionDone = fraction,
            etaEpochMs = eta,
            elapsedS = s.elapsedS
        )
        buffer.addLast(sample)
        while (buffer.size > maxBuffer) buffer.removeFirst()
        flush()
    }

    private suspend fun flush() {
        if (buffer.isEmpty()) return
        val batch = buffer.toList()
        val res = repo.ingest(IngestDto(shareToken, batch))
        if (res.isSuccess) {
            // tick()/flush() ne s'exécutent que dans l'unique coroutine du job → pas de concurrence.
            repeat(batch.size) { if (buffer.isNotEmpty()) buffer.removeFirst() }
        }
    }

    fun stop() {
        job?.cancel()
        scope.launch(Dispatchers.IO) {
            flush()
            repo.stopSession(StopSessionDto(shareToken, System.currentTimeMillis()))
        }
    }
}
