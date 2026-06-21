package com.example.compteur.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compteur.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.compteur.data.db.entity.SynchronizedDeviceEntity
import com.example.compteur.service.SensorType
import com.example.compteur.ui.theme.PureBlack
import com.example.compteur.data.repository.SettingsRepository
import com.example.compteur.data.repository.MapStyle
import com.example.compteur.data.repository.HrCalculationMode
import androidx.compose.ui.semantics.Role
import android.content.Intent
import com.juul.kable.State
import org.maplibre.android.offline.OfflineRegion
import android.net.Uri
import com.example.compteur.utils.StravaConstants
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToMapSelection: () -> Unit
) {
    val isScanning by viewModel.isScanning.collectAsState()
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()
    val synchronizedDevices by viewModel.synchronizedDevices.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()
    val connectionStates by viewModel.connectionStates.collectAsState()
    val deviceCapabilities by viewModel.deviceCapabilities.collectAsState()
    val currentMapStyle by viewModel.currentMapStyle.collectAsState()
    val maxHeartRate by viewModel.maxHeartRate.collectAsState()
    val hrCalculationMode by viewModel.hrCalculationMode.collectAsState()
    val restingHeartRate by viewModel.restingHeartRate.collectAsState()
    val lactateThreshold by viewModel.lactateThreshold.collectAsState()
    val zoneBoundaries by viewModel.zoneBoundaries.collectAsState()
    
    val isStravaConnected by viewModel.isStravaConnected.collectAsState()

    val liveTrackingEnabled by viewModel.liveTrackingEnabled.collectAsState()
    val liveTrackingBaseUrl by viewModel.liveTrackingBaseUrl.collectAsState()
    val liveTrackingIngestKey by viewModel.liveTrackingIngestKey.collectAsState()
    val lastShareUrl by viewModel.lastShareUrl.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.infoMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.startScan()
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Général", "Zones d'intensité", "Intégrations")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PureBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PureBlack)
        ) {
            Text(
                text = stringResource(R.string.nav_settings),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = PureBlack,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (selectedTabIndex == 0) {
                    GeneralSettingsSection(
                        viewModel = viewModel,
                        currentMapStyle = currentMapStyle,
                        synchronizedDevices = synchronizedDevices,
                        connectionStates = connectionStates,
                        deviceCapabilities = deviceCapabilities,
                        sensorData = sensorData,
                        isScanning = isScanning,
                        discoveredDevices = discoveredDevices,
                        bluetoothPermissions = bluetoothPermissions,
                        permissionLauncher = permissionLauncher,
                        onNavigateToMapSelection = onNavigateToMapSelection
                    )
                } else if (selectedTabIndex == 1) {
                    IntensityZonesSettingsSection(
                        viewModel = viewModel,
                        hrCalculationMode = hrCalculationMode,
                        maxHeartRate = maxHeartRate,
                        restingHeartRate = restingHeartRate,
                        lactateThreshold = lactateThreshold,
                        zoneBoundaries = zoneBoundaries
                    )
                } else {
                    IntegrationsSettingsSection(
                        isStravaConnected = isStravaConnected,
                        onConnectStrava = {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW, 
                                    Uri.parse("${StravaConstants.AUTH_URL}?client_id=${StravaConstants.CLIENT_ID}&response_type=code&redirect_uri=${StravaConstants.REDIRECT_URI}&approval_prompt=force&scope=${StravaConstants.SCOPE}")
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        onDisconnectStrava = { viewModel.disconnectStrava() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    LiveTrackingSettingsSection(
                        enabled = liveTrackingEnabled,
                        baseUrl = liveTrackingBaseUrl,
                        ingestKey = liveTrackingIngestKey,
                        lastShareUrl = lastShareUrl,
                        onEnabledChange = { viewModel.setLiveTrackingEnabled(it) },
                        onBaseUrlChange = { viewModel.setLiveTrackingBaseUrl(it) },
                        onIngestKeyChange = { viewModel.setLiveTrackingIngestKey(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun GeneralSettingsSection(
    viewModel: SettingsViewModel,
    currentMapStyle: MapStyle,
    synchronizedDevices: List<SynchronizedDeviceEntity>,
    connectionStates: Map<String, State>,
    deviceCapabilities: Map<String, Set<SensorType>>,
    sensorData: com.example.compteur.service.BleSensorData,
    isScanning: Boolean,
    discoveredDevices: List<com.juul.kable.Advertisement>,
    bluetoothPermissions: Array<String>,
    permissionLauncher: androidx.activity.compose.ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    onNavigateToMapSelection: () -> Unit
) {
    val context = LocalContext.current

    // --- Section Style de carte ---
    Text(
        text = "Style de carte",
        style = MaterialTheme.typography.titleMedium,
        color = Color.LightGray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        MapStyle.entries.filter { it.isAvailable }.forEach { style ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = (style == currentMapStyle),
                        onClick = { viewModel.setMapStyle(style) },
                        role = Role.RadioButton
                    )
                    .padding(end = 16.dp)
            ) {
                RadioButton(
                    selected = (style == currentMapStyle),
                    onClick = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = style.title, color = Color.White)
            }
        }
    }

    HorizontalDivider(color = Color.DarkGray)
    Spacer(modifier = Modifier.height(16.dp))

    // --- Section Cartes Hors-ligne ---
    OfflineMapsSection(viewModel, onNavigateToMapSelection)

    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(color = Color.DarkGray)
    Spacer(modifier = Modifier.height(16.dp))

    // --- Section Synchronisés ---
    Text(
        text = "Appareils synchronisés",
        style = MaterialTheme.typography.titleMedium,
        color = Color.LightGray,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    if (synchronizedDevices.isEmpty()) {
        Text(
            text = "Aucun appareil synchronisé",
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    } else {
        synchronizedDevices.forEach { device ->
            SynchronizedDeviceItem(
                device = device,
                status = connectionStates[device.macAddress] ?: State.Disconnected(),
                detectedTypes = deviceCapabilities[device.macAddress] ?: emptySet(),
                value = when (val type = SensorType.valueOf(device.type)) {
                    SensorType.HEART_RATE -> sensorData.heartRate?.let { "$it bpm" }
                    SensorType.SPEED -> sensorData.speedKph?.let { "${"%.1f".format(it)} km/h" }
                    SensorType.CADENCE -> sensorData.cadence?.let { "$it rpm" }
                    SensorType.CADENCE_SPEED -> {
                        val cadenceStr = sensorData.cadence?.let { "$it rpm" }
                        val speedStr = sensorData.speedKph?.let { "${"%.1f".format(it)} km/h" }
                        if (cadenceStr != null || speedStr != null) {
                            listOfNotNull(cadenceStr, speedStr).joinToString(" - ")
                        } else null
                    }
                    SensorType.POWER -> sensorData.power?.let { "$it W" }
                },
                onConnect = { viewModel.connectDevice(device.macAddress, SensorType.valueOf(device.type)) },
                onDisconnect = { viewModel.disconnectDevice(device.macAddress) },
                onRemove = { viewModel.unsyncDevice(device) }
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(color = Color.DarkGray)
    Spacer(modifier = Modifier.height(16.dp))

    // --- Section Scan ---
    Button(
        onClick = {
            if (isScanning) {
                viewModel.stopScan()
            } else {
                val missingPermissions = bluetoothPermissions.filter {
                    ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
                }
                if (missingPermissions.isEmpty()) {
                    viewModel.startScan()
                } else {
                    permissionLauncher.launch(bluetoothPermissions)
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (isScanning) "Arrêter le scan" else "Rechercher des capteurs")
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (isScanning) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
    }

    Text(
        text = "Appareils trouvés",
        style = MaterialTheme.typography.titleMedium,
        color = Color.LightGray,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        discoveredDevices.forEach { device ->
            DeviceDiscoveryItem(
                name = device.name ?: "Appareil Inconnu",
                address = device.identifier,
                onSync = { type -> viewModel.syncDevice(device, type) }
            )
        }
    }
}

@Composable
fun IntensityZonesSettingsSection(
    viewModel: SettingsViewModel,
    hrCalculationMode: HrCalculationMode,
    maxHeartRate: Int,
    restingHeartRate: Int,
    lactateThreshold: Int,
    zoneBoundaries: List<com.example.compteur.service.ZoneBoundary>
) {
    Text(
        text = "Méthode de calcul",
        style = MaterialTheme.typography.titleMedium,
        color = Color.LightGray,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        HrCalculationMode.entries.forEach { mode ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (mode == hrCalculationMode),
                        onClick = { viewModel.setHrCalculationMode(mode) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = (mode == hrCalculationMode),
                    onClick = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = mode.title, color = Color.White)
            }
        }
    }

    HorizontalDivider(color = Color.DarkGray)
    Spacer(modifier = Modifier.height(16.dp))

    // Paramètres dynamiques selon le mode choisi
    if (hrCalculationMode == HrCalculationMode.FCM || hrCalculationMode == HrCalculationMode.KARVONEN) {
        Text(
            text = "Fréquence Cardiaque Maximale (FCmax)",
            style = MaterialTheme.typography.titleMedium,
            color = Color.LightGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = maxHeartRate.toFloat(),
                onValueChange = { viewModel.setMaxHeartRate(it.toInt()) },
                valueRange = 100f..220f,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "$maxHeartRate bpm", color = Color.White)
        }
    }

    if (hrCalculationMode == HrCalculationMode.KARVONEN) {
        Text(
            text = "Fréquence Cardiaque au Repos",
            style = MaterialTheme.typography.titleMedium,
            color = Color.LightGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = restingHeartRate.toFloat(),
                onValueChange = { viewModel.setRestingHeartRate(it.toInt()) },
                valueRange = 30f..100f,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "$restingHeartRate bpm", color = Color.White)
        }
    }

    if (hrCalculationMode == HrCalculationMode.LACTATE_THRESHOLD) {
        Text(
            text = "Seuil Lactique",
            style = MaterialTheme.typography.titleMedium,
            color = Color.LightGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = lactateThreshold.toFloat(),
                onValueChange = { viewModel.setLactateThreshold(it.toInt()) },
                valueRange = 100f..200f,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "$lactateThreshold bpm", color = Color.White)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(color = Color.DarkGray)
    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Résumé des Zones d'Intensité",
        style = MaterialTheme.typography.titleMedium,
        color = Color.LightGray,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
            .padding(12.dp)
    ) {
        if (zoneBoundaries.isEmpty()) {
            Text(text = "Chargement...", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        } else {
            zoneBoundaries.forEach { boundary ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(boundary.color, shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = boundary.name, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        text = "${boundary.minBpm} - ${if (boundary.maxBpm > 220) "Max" else boundary.maxBpm} bpm",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun SynchronizedDeviceItem(
    device: SynchronizedDeviceEntity,
    status: State,
    detectedTypes: Set<SensorType>,
    value: String?,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = device.name, style = MaterialTheme.typography.titleSmall, color = Color.White)
                val typeLabel = when (SensorType.valueOf(device.type)) {
                    SensorType.HEART_RATE -> "Fréquence Cardiaque"
                    SensorType.SPEED -> "Vitesse"
                    SensorType.CADENCE -> "Cadence"
                    SensorType.CADENCE_SPEED -> "Vitesse & Cadence"
                    SensorType.POWER -> "Puissance"
                }
                Text(text = typeLabel, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                
                if (status is State.Connected && detectedTypes.isNotEmpty()) {
                    val savedType = SensorType.valueOf(device.type)
                    val isMismatch = when (savedType) {
                        SensorType.SPEED -> !detectedTypes.contains(SensorType.SPEED)
                        SensorType.CADENCE -> !detectedTypes.contains(SensorType.CADENCE)
                        SensorType.CADENCE_SPEED -> !detectedTypes.contains(SensorType.CADENCE_SPEED)
                        else -> false
                    }
                    
                    if (isMismatch) {
                        val detectedLabel = if (detectedTypes.contains(SensorType.SPEED)) "Vitesse" else if (detectedTypes.contains(SensorType.CADENCE)) "Cadence" else "Autre"
                        Text(
                            text = "Mode détecté : $detectedLabel",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Yellow
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = if (status is State.Connected) Color.Green else Color.Gray
                    Box(modifier = Modifier.size(8.dp).background(statusColor, shape = CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = status.toString(), style = MaterialTheme.typography.bodySmall, color = statusColor)
                }
            }
            
            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Row {
                if (status is State.Connected) {
                    IconButton(onClick = onDisconnect) {
                        Icon(Icons.Default.Close, contentDescription = "Disconnect")
                    }
                } else if (status !is State.Connecting) {
                    IconButton(onClick = onConnect) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Connect")
                    }
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun DeviceDiscoveryItem(name: String, address: String, onSync: (SensorType) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleSmall, color = Color.White)
                Text(text = address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Row {
                TextButton(onClick = { onSync(SensorType.HEART_RATE) }) { Text("HR") }
                TextButton(onClick = { onSync(SensorType.SPEED) }) { Text("SPD") }
                TextButton(onClick = { onSync(SensorType.CADENCE) }) { Text("CAD") }
                TextButton(onClick = { onSync(SensorType.CADENCE_SPEED) }) { Text("BOTH") }
            }
        }
    }
}

@Composable
fun OfflineMapsSection(viewModel: SettingsViewModel, onNavigateToMapSelection: () -> Unit) {
    val offlineRegions by viewModel.offlineRegions.collectAsState()
    val downloadStates by viewModel.downloadStates.collectAsState()
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cartes hors-ligne",
                style = MaterialTheme.typography.titleMedium,
                color = Color.LightGray
            )
            TextButton(onClick = onNavigateToMapSelection) {
                Text("Télécharger la zone")
            }
        }
        if (offlineRegions.isEmpty() && downloadStates.isEmpty()) {
            Text(
                text = "Aucune carte téléchargée",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Active downloads
        downloadStates.forEach { (id, state) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Téléchargement : ${state.name}", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (state.progress / 100.0).toFloat() },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.error != null) {
                        Text(text = "Erreur : ${state.error}", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Downloaded regions
        offlineRegions.forEach { region ->
            val name = viewModel.getRegionName(region)
            OfflineRegionItem(
                name = name,
                onDelete = { viewModel.deleteOfflineRegion(region) }
            )
        }
    }
}

@Composable
fun OfflineRegionItem(name: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium, color = Color.White)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun IntegrationsSettingsSection(
    isStravaConnected: Boolean,
    onConnectStrava: () -> Unit,
    onDisconnectStrava: () -> Unit
) {
    Text(
        text = "Strava",
        style = MaterialTheme.typography.titleMedium,
        color = Color.LightGray,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isStravaConnected) "Connecté" else "Non connecté",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isStravaConnected) Color.Green else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Synchronisez vos activités directement avec Strava.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Button(
                    onClick = if (isStravaConnected) onDisconnectStrava else onConnectStrava,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isStravaConnected) Color.DarkGray else Color(0xFFFC4C02)
                    )
                ) {
                    Text(if (isStravaConnected) "Déconnecter" else "Se connecter", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LiveTrackingSettingsSection(
    enabled: Boolean,
    baseUrl: String,
    ingestKey: String,
    lastShareUrl: String?,
    onEnabledChange: (Boolean) -> Unit,
    onBaseUrlChange: (String) -> Unit,
    onIngestKeyChange: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    Text(
        text = "Suivi live public",
        style = MaterialTheme.typography.titleMedium,
        color = Color.LightGray,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Partager mes courses en direct",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Envoie position et compteurs toutes les ~15 s vers votre serveur. Impact batterie minimal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Switch(checked = enabled, onCheckedChange = onEnabledChange)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = baseUrl,
                onValueChange = onBaseUrlChange,
                label = { Text("URL du serveur") },
                placeholder = { Text("https://suivi.exemple.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ingestKey,
                onValueChange = onIngestKeyChange,
                label = { Text("Clé d'ingestion") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (!lastShareUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Dernier lien de partage",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.LightGray
                )
                Text(
                    text = lastShareUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    TextButton(onClick = { clipboard.setText(AnnotatedString(lastShareUrl)) }) {
                        Text("Copier")
                    }
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, lastShareUrl)
                        }
                        context.startActivity(Intent.createChooser(intent, "Partager le lien"))
                    }) {
                        Text("Partager")
                    }
                }
            }
        }
    }
}
