package com.example.compteur.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.longPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class MapStyle(val title: String, val url: String) {
    CLASSIC("Classique", "https://tiles.openfreemap.org/styles/liberty"),
    SATELLITE("Satellite", "https://api.maptiler.com/maps/satellite/style.json?key=PLACEHOLDER");

    // Un style nécessitant une clé API non renseignée (PLACEHOLDER) n'est pas
    // sélectionnable : son chargement et son téléchargement hors-ligne échoueraient (401).
    val isAvailable: Boolean
        get() = !url.contains("PLACEHOLDER")
}

enum class HrCalculationMode(val title: String) {
    FCM("Fréquence Cardiaque Maximale"),
    KARVONEN("Méthode de Karvonen"),
    LACTATE_THRESHOLD("Seuil Lactique")
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val MAP_STYLE_KEY = stringPreferencesKey("map_style")
    private val MAX_HR_KEY = intPreferencesKey("max_heart_rate")
    private val HR_CALCULATION_MODE_KEY = stringPreferencesKey("hr_calculation_mode")
    private val RESTING_HR_KEY = intPreferencesKey("resting_heart_rate")
    private val LACTATE_THRESHOLD_KEY = intPreferencesKey("lactate_threshold")

    private val STRAVA_ACCESS_TOKEN_KEY = stringPreferencesKey("strava_access_token")
    private val STRAVA_REFRESH_TOKEN_KEY = stringPreferencesKey("strava_refresh_token")
    private val STRAVA_EXPIRES_AT_KEY = longPreferencesKey("strava_expires_at")

    private val LIVE_TRACKING_ENABLED_KEY = booleanPreferencesKey("live_tracking_enabled")
    private val LIVE_TRACKING_BASE_URL_KEY = stringPreferencesKey("live_tracking_base_url")
    private val LIVE_TRACKING_INGEST_KEY_KEY = stringPreferencesKey("live_tracking_ingest_key")
    private val LIVE_TRACKING_LAST_SHARE_URL_KEY = stringPreferencesKey("live_tracking_last_share_url")

    private val DEFAULT_LIVE_TRACKING_BASE_URL = "https://bikelive.quentin-desmettre.fr"

    val mapStyleFlow: Flow<MapStyle> = context.dataStore.data.map { preferences ->
        val styleName = preferences[MAP_STYLE_KEY] ?: MapStyle.CLASSIC.name
        try {
            MapStyle.valueOf(styleName)
        } catch (e: Exception) {
            MapStyle.CLASSIC
        }
    }

    suspend fun setMapStyle(style: MapStyle) {
        context.dataStore.edit { preferences ->
            preferences[MAP_STYLE_KEY] = style.name
        }
    }

    val maxHeartRateFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[MAX_HR_KEY] ?: 190
    }

    suspend fun setMaxHeartRate(maxHr: Int) {
        context.dataStore.edit { preferences ->
            preferences[MAX_HR_KEY] = maxHr
        }
    }

    val hrCalculationModeFlow: Flow<HrCalculationMode> = context.dataStore.data.map { preferences ->
        val modeName = preferences[HR_CALCULATION_MODE_KEY] ?: HrCalculationMode.FCM.name
        try {
            HrCalculationMode.valueOf(modeName)
        } catch (e: Exception) {
            HrCalculationMode.FCM
        }
    }

    suspend fun setHrCalculationMode(mode: HrCalculationMode) {
        context.dataStore.edit { preferences ->
            preferences[HR_CALCULATION_MODE_KEY] = mode.name
        }
    }

    val restingHeartRateFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[RESTING_HR_KEY] ?: 60
    }

    suspend fun setRestingHeartRate(hr: Int) {
        context.dataStore.edit { preferences ->
            preferences[RESTING_HR_KEY] = hr
        }
    }

    val lactateThresholdFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LACTATE_THRESHOLD_KEY] ?: 160
    }

    suspend fun setLactateThreshold(threshold: Int) {
        context.dataStore.edit { preferences ->
            preferences[LACTATE_THRESHOLD_KEY] = threshold
        }
    }

    val stravaAccessTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[STRAVA_ACCESS_TOKEN_KEY]
    }

    val stravaRefreshTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[STRAVA_REFRESH_TOKEN_KEY]
    }

    val stravaExpiresAtFlow: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[STRAVA_EXPIRES_AT_KEY]
    }

    suspend fun saveStravaTokens(accessToken: String, refreshToken: String, expiresAt: Long) {
        context.dataStore.edit { preferences ->
            preferences[STRAVA_ACCESS_TOKEN_KEY] = accessToken
            preferences[STRAVA_REFRESH_TOKEN_KEY] = refreshToken
            preferences[STRAVA_EXPIRES_AT_KEY] = expiresAt
        }
    }

    suspend fun clearStravaTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(STRAVA_ACCESS_TOKEN_KEY)
            preferences.remove(STRAVA_REFRESH_TOKEN_KEY)
            preferences.remove(STRAVA_EXPIRES_AT_KEY)
        }
    }

    // --- Suivi live public ---

    val liveTrackingEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[LIVE_TRACKING_ENABLED_KEY] ?: false
    }

    suspend fun setLiveTrackingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LIVE_TRACKING_ENABLED_KEY] = enabled
        }
    }

    val liveTrackingBaseUrlFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LIVE_TRACKING_BASE_URL_KEY] ?: DEFAULT_LIVE_TRACKING_BASE_URL
    }

    suspend fun setLiveTrackingBaseUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[LIVE_TRACKING_BASE_URL_KEY] = url
        }
    }

    val liveTrackingIngestKeyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LIVE_TRACKING_INGEST_KEY_KEY] ?: ""
    }

    suspend fun setLiveTrackingIngestKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[LIVE_TRACKING_INGEST_KEY_KEY] = key
        }
    }

    val lastShareUrlFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LIVE_TRACKING_LAST_SHARE_URL_KEY]
    }

    suspend fun setLastShareUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[LIVE_TRACKING_LAST_SHARE_URL_KEY] = url
        }
    }
}
