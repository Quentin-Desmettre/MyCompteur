package com.example.compteur.data.repository

import com.example.compteur.data.api.IngestDto
import com.example.compteur.data.api.LiveTrackingApi
import com.example.compteur.data.api.StartSessionDto
import com.example.compteur.data.api.StopSessionDto
import com.example.compteur.domain.repository.LiveTrackingRepository
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveTrackingRepositoryImpl @Inject constructor(
    private val api: LiveTrackingApi,
    private val settingsRepository: SettingsRepository
) : LiveTrackingRepository {

    override suspend fun isEnabled(): Boolean =
        settingsRepository.liveTrackingEnabledFlow.firstOrNull() ?: false

    override suspend fun baseUrl(): String? =
        settingsRepository.liveTrackingBaseUrlFlow.firstOrNull()
            ?.trim()?.trimEnd('/')?.takeIf { it.isNotBlank() }

    /** Renvoie (baseUrl, ingestKey) si le suivi est activé et configuré, sinon null. */
    private suspend fun config(): Pair<String, String>? {
        if (!isEnabled()) return null
        val base = baseUrl() ?: return null
        val key = settingsRepository.liveTrackingIngestKeyFlow.firstOrNull()
            ?.trim()?.takeIf { it.isNotBlank() } ?: return null
        return base to key
    }

    private suspend fun exec(call: suspend (String, String) -> Response<Unit>): Result<Unit> {
        val (base, key) = config()
            ?: return Result.failure(IllegalStateException("Suivi live désactivé ou non configuré"))
        return try {
            val resp = call(base, key)
            if (resp.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("HTTP ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startSession(body: StartSessionDto): Result<Unit> =
        exec { base, key -> api.startSession("$base/api/session/start", key, body) }

    override suspend fun ingest(body: IngestDto): Result<Unit> =
        exec { base, key -> api.ingest("$base/api/ingest", key, body) }

    override suspend fun stopSession(body: StopSessionDto): Result<Unit> =
        exec { base, key -> api.stopSession("$base/api/session/stop", key, body) }
}
