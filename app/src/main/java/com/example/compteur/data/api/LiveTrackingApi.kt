package com.example.compteur.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

// --- DTO envoyés au backend de suivi live (miroir des modèles Pydantic côté serveur) ---

@JsonClass(generateAdapter = true)
data class GeoPointDto(
    val lat: Double,
    val lng: Double,
    val ele: Float? = null
)

@JsonClass(generateAdapter = true)
data class StartSessionDto(
    @Json(name = "share_token") val shareToken: String,
    @Json(name = "athlete_name") val athleteName: String?,
    @Json(name = "route_name") val routeName: String?,
    @Json(name = "total_route_m") val totalRouteMeters: Double,
    @Json(name = "route_points") val routePoints: List<GeoPointDto>,
    @Json(name = "started_at") val startedAt: Long
)

@JsonClass(generateAdapter = true)
data class SampleDto(
    val t: Long,
    val lat: Double?,
    val lng: Double?,
    @Json(name = "speed_kph") val speedKph: Float?,
    val hr: Int?,
    val cadence: Int?,
    val power: Int?,
    @Json(name = "ascent_m") val ascentM: Float?,
    @Json(name = "dist_total_m") val distTotalM: Float?,
    @Json(name = "dist_along_m") val distAlongM: Double?,
    @Json(name = "dist_remaining_m") val distRemainingM: Double?,
    @Json(name = "fraction_done") val fractionDone: Double?,
    @Json(name = "eta_epoch_ms") val etaEpochMs: Long?,
    @Json(name = "elapsed_s") val elapsedS: Long?
)

@JsonClass(generateAdapter = true)
data class IngestDto(
    @Json(name = "share_token") val shareToken: String,
    val samples: List<SampleDto>
)

@JsonClass(generateAdapter = true)
data class StopSessionDto(
    @Json(name = "share_token") val shareToken: String,
    @Json(name = "ended_at") val endedAt: Long
)

/**
 * L'URL de base étant un réglage utilisateur, on passe l'URL complète via [@Url] plutôt que de
 * figer le baseUrl du Retrofit. La clé d'ingestion secrète part dans l'en-tête X-Ingest-Key.
 */
interface LiveTrackingApi {

    @POST
    suspend fun startSession(
        @Url url: String,
        @Header("X-Ingest-Key") key: String,
        @Body body: StartSessionDto
    ): Response<Unit>

    @POST
    suspend fun ingest(
        @Url url: String,
        @Header("X-Ingest-Key") key: String,
        @Body body: IngestDto
    ): Response<Unit>

    @POST
    suspend fun stopSession(
        @Url url: String,
        @Header("X-Ingest-Key") key: String,
        @Body body: StopSessionDto
    ): Response<Unit>
}
