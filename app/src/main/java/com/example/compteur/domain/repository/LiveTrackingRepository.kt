package com.example.compteur.domain.repository

import com.example.compteur.data.api.IngestDto
import com.example.compteur.data.api.StartSessionDto
import com.example.compteur.data.api.StopSessionDto

/**
 * Pousse la télémétrie d'une activité en cours vers un backend distant (site public).
 * Les appels ne font rien si le suivi live est désactivé ou non configuré.
 */
interface LiveTrackingRepository {
    suspend fun isEnabled(): Boolean
    suspend fun baseUrl(): String?
    suspend fun startSession(body: StartSessionDto): Result<Unit>
    suspend fun ingest(body: IngestDto): Result<Unit>
    suspend fun stopSession(body: StopSessionDto): Result<Unit>
}
