package com.example.compteur.service

import android.content.Context
import android.util.Log
import com.example.compteur.data.repository.MapStyle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.offline.OfflineManager
import org.maplibre.android.offline.OfflineRegion
import org.maplibre.android.offline.OfflineRegionError
import org.maplibre.android.offline.OfflineRegionStatus
import org.maplibre.android.offline.OfflineTilePyramidRegionDefinition
import javax.inject.Inject
import javax.inject.Singleton

data class OfflineRegionDownloadState(
    val name: String,
    val progress: Double = 0.0,
    val isDownloading: Boolean = false,
    val error: String? = null
)

@Singleton
class OfflineMapManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "OfflineMapManager"
    private val offlineManager = OfflineManager.getInstance(context)

    private val _downloadStates = MutableStateFlow<Map<Long, OfflineRegionDownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<Long, OfflineRegionDownloadState>> = _downloadStates.asStateFlow()

    private val _regions = MutableStateFlow<List<OfflineRegion>>(emptyList())
    val regions: StateFlow<List<OfflineRegion>> = _regions.asStateFlow()

    init {
        // La limite par défaut de MapLibre est de 6000 tuiles, largement insuffisante
        // pour une zone de ville sur plusieurs niveaux de zoom. On la relève pour éviter
        // les échecs silencieux (mapboxTileCountLimitExceeded).
        offlineManager.setOfflineMapboxTileCountLimit(50_000L)
        refreshRegions()
    }

    fun refreshRegions() {
        offlineManager.listOfflineRegions(object : OfflineManager.ListOfflineRegionsCallback {
            override fun onList(offlineRegions: Array<OfflineRegion>?) {
                _regions.value = offlineRegions?.toList() ?: emptyList()
            }

            override fun onError(error: String) {
                Log.e(TAG, "Error listing regions: $error")
            }
        })
    }

    fun downloadRegion(
        name: String,
        bounds: LatLngBounds,
        styleUrl: String,
        isSatellite: Boolean
    ) {
        val pixelRatio = context.resources.displayMetrics.density
        
        // Define zoom levels based on map type.
        // minZoom 0 téléchargeait inutilement des tuiles "monde entier" et gonflait le
        // compte de tuiles. On part d'un zoom régional. Pour le vectoriel, au-delà de z16
        // openfreemap sur-zoome (pas de tuiles supplémentaires utiles).
        val minZoom = 10.0
        val maxZoom = if (isSatellite) 15.0 else 16.0

        val definition = OfflineTilePyramidRegionDefinition(
            styleUrl,
            bounds,
            minZoom,
            maxZoom,
            pixelRatio
        )

        val metadata: ByteArray
        try {
            val jsonObject = JSONObject()
            jsonObject.put("FIELD_REGION_NAME", name)
            metadata = jsonObject.toString().toByteArray(Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encode metadata: ${e.message}")
            return
        }

        offlineManager.createOfflineRegion(
            definition,
            metadata,
            object : OfflineManager.CreateOfflineRegionCallback {
                override fun onCreate(offlineRegion: OfflineRegion) {
                    startDownload(offlineRegion, name)
                }

                override fun onError(error: String) {
                    Log.e(TAG, "Error creating region: $error")
                }
            }
        )
    }

    private fun startDownload(region: OfflineRegion, name: String) {
        val regionId = region.id
        _downloadStates.update { it + (regionId to OfflineRegionDownloadState(name = name, isDownloading = true)) }

        region.setObserver(object : OfflineRegion.OfflineRegionObserver {
            override fun onStatusChanged(status: OfflineRegionStatus) {
                val percentage = if (status.requiredResourceCount > 0) {
                    100.0 * status.completedResourceCount / status.requiredResourceCount
                } else {
                    0.0
                }

                Log.d(TAG, "Region $name progress: $percentage%")

                if (status.isComplete) {
                    region.setDownloadState(OfflineRegion.STATE_INACTIVE)
                    region.setObserver(null)
                    _downloadStates.update { it - regionId }
                    refreshRegions()
                } else {
                    _downloadStates.update { states ->
                        val current = states[regionId] ?: return@update states
                        states + (regionId to current.copy(progress = percentage))
                    }
                }
            }

            override fun onError(error: OfflineRegionError) {
                Log.e(TAG, "Download error for $name: ${error.message}")
                _downloadStates.update { states ->
                    val current = states[regionId] ?: return@update states
                    states + (regionId to current.copy(isDownloading = false, error = error.message))
                }
            }

            override fun mapboxTileCountLimitExceeded(limit: Long) {
                Log.e(TAG, "Tile limit exceeded for $name")
                _downloadStates.update { states ->
                    val current = states[regionId] ?: return@update states
                    states + (regionId to current.copy(isDownloading = false, error = "Limite de tuiles dépassée"))
                }
            }
        })

        region.setDownloadState(OfflineRegion.STATE_ACTIVE)
    }

    fun deleteRegion(region: OfflineRegion) {
        region.delete(object : OfflineRegion.OfflineRegionDeleteCallback {
            override fun onDelete() {
                refreshRegions()
            }

            override fun onError(error: String) {
                Log.e(TAG, "Error deleting region: $error")
            }
        })
    }

    fun getRegionName(region: OfflineRegion): String {
        return try {
            val metadata = region.metadata
            val json = String(metadata, Charsets.UTF_8)
            val jsonObject = JSONObject(json)
            jsonObject.getString("FIELD_REGION_NAME")
        } catch (e: Exception) {
            "Région ${region.id}"
        }
    }
}
