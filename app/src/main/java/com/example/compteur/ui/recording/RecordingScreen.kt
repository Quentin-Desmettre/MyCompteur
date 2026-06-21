package com.example.compteur.ui.recording

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.compteur.ui.components.MapComponent
import com.example.compteur.ui.theme.PureBlack
import com.example.compteur.ui.theme.AccentCyan
import com.example.compteur.ui.theme.AccentGreen
import com.example.compteur.ui.theme.AccentRed
import com.example.compteur.utils.ServiceUtils
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.shadow

@Composable
fun RecordingScreen(
    viewModel: RecordingViewModel = hiltViewModel(),
    onFullscreenChange: (Boolean) -> Unit = {}
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val plannedPoints by viewModel.plannedPoints.collectAsState()
    val totalDistance by viewModel.totalDistance.collectAsState()
    val mapStyleUrl by viewModel.mapStyleUrl.collectAsState()
    val recordedPath by viewModel.recordedPath.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()
    val displaySpeedKph by viewModel.displaySpeedKph.collectAsState()
    val displayDistanceKm by viewModel.displayDistanceKm.collectAsState()
    val elapsedTime by viewModel.elapsedTimeSeconds.collectAsState()
    val heartRateZoneInfo by viewModel.heartRateZoneInfo.collectAsState()

    var isFullscreen by remember { mutableStateOf(false) }
    var mapCameraMode by remember { mutableIntStateOf(0) } // 0: Free, 1: Follow Position, 2: Follow Position & Bearing
    
    val context = LocalContext.current
    
    var showGpsDialog by remember { mutableStateOf(false) }
    var showBluetoothDialog by remember { mutableStateOf(false) }
    var showStopDialog by remember { mutableStateOf(false) }

    val bluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (ServiceUtils.isBluetoothEnabled(context) && ServiceUtils.isGpsEnabled(context)) {
            viewModel.startRecording()
        }
    }
    
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (locationGranted) {
            viewModel.startRecording()
        }
    }

    fun checkAndStart() {
        val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        
        val needsNotificationPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        val hasNotificationPermission = if (needsNotificationPermission) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true

        if ((hasFineLocation || hasCoarseLocation) && hasNotificationPermission) {
            if (!ServiceUtils.isGpsEnabled(context)) {
                showGpsDialog = true
                return
            }
            if (!ServiceUtils.isBluetoothEnabled(context)) {
                showBluetoothDialog = true
                return
            }
            viewModel.startRecording()
        } else {
            val permissions = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (needsNotificationPermission) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            permissionsLauncher.launch(permissions.toTypedArray())
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(PureBlack)) {
        // === MAP (full background) ===
        MapComponent(
            modifier = Modifier.fillMaxSize(),
            plannedPoints = if (plannedPoints.isNotEmpty()) plannedPoints else null,
            recordedPath = if (recordedPath.isNotEmpty()) recordedPath else null,
            currentLocation = currentLocation,
            cameraMode = mapCameraMode,
            onCameraModeChange = { mapCameraMode = it }
        )

        // === TOP-RIGHT: Fullscreen button ===
        FloatingActionButton(
            onClick = {
                isFullscreen = !isFullscreen
                onFullscreenChange(isFullscreen)
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
                .size(44.dp),
            containerColor = Color.Black.copy(alpha = 0.7f),
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                contentDescription = if (isFullscreen) "Exit fullscreen" else "Fullscreen",
                modifier = Modifier.size(22.dp)
            )
        }

        // === BOTTOM-LEFT: Sensor metric bubbles (vertical column, bottom to top) ===
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Elapsed time (always shown when recording)
            if (isRecording) {
                MetricBubble(
                    value = formatTime(elapsedTime),
                    unit = "",
                    color = Color.White
                )
            }

            // Power (only if sensor connected)
            sensorData.power?.let { power ->
                MetricBubble(
                    value = "$power",
                    unit = "W",
                    color = Color(0xFFFF9800)
                )
            }

            // Heart rate (only if sensor connected)
            sensorData.heartRate?.let { hr ->
                val zoneInfo = heartRateZoneInfo
                val bgColor = zoneInfo?.bgColor ?: Color.Black.copy(alpha = 0.80f)
                val nextColor = zoneInfo?.nextColor
                val progress = zoneInfo?.progress

                val textColor = if (bgColor == Color(0xFFFFEB3B)) Color.Black else Color.White

                MetricBubble(
                    value = "$hr",
                    unit = "bpm",
                    color = textColor,
                    backgroundColor = bgColor,
                    progress = progress,
                    progressColor = nextColor
                )
            }

            // Cadence (only if sensor connected)
            sensorData.cadence?.let { cadence ->
                MetricBubble(
                    value = "$cadence",
                    unit = "rpm",
                    color = AccentCyan
                )
            }

            // Distance (always shown)
            MetricBubble(
                value = "%.1f".format(displayDistanceKm),
                unit = "km",
                color = AccentGreen
            )

            // Speed (always shown — BLE or GPS fallback)
            MetricBubble(
                value = "%.1f".format(displaySpeedKph),
                unit = "km/h",
                color = Color.White
            )
        }

        // === BOTTOM-RIGHT: Control buttons (vertical stack) ===
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stop button (only when recording)
            if (isRecording) {
                FloatingActionButton(
                    onClick = { showStopDialog = true },
                    modifier = Modifier.size(48.dp),
                    containerColor = AccentRed,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop recording",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Pause / Resume button
                FloatingActionButton(
                    onClick = { viewModel.togglePause() },
                    modifier = Modifier.size(48.dp),
                    containerColor = if (isPaused) AccentGreen else Color(0xFFFF9800),
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Center camera + orientation button (always shown)
            FloatingActionButton(
                onClick = { mapCameraMode = (mapCameraMode + 1) % 3 },
                modifier = Modifier.size(48.dp),
                containerColor = if (mapCameraMode != 0) AccentCyan else Color.Black.copy(alpha = 0.7f),
                contentColor = if (mapCameraMode != 0) Color.Black else Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(
                    imageVector = when (mapCameraMode) {
                        2 -> Icons.Default.Explore
                        else -> Icons.Default.MyLocation
                    },
                    contentDescription = when (mapCameraMode) {
                        1 -> "Follow my location"
                        2 -> "Follow my location and orientation"
                        else -> "Free map"
                    },
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // === CENTER-BOTTOM: GO button (only before recording starts) ===
        if (!isRecording) {
            LargeFloatingActionButton(
                onClick = { checkAndStart() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                containerColor = AccentGreen,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Text(
                    text = "GO",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )
            }
        }

        // === DIALOGS ===
        if (showGpsDialog) {
            AlertDialog(
                onDismissRequest = { showGpsDialog = false },
                title = { Text("GPS désactivé") },
                text = { Text("La localisation est nécessaire pour enregistrer votre parcours. Veuillez l'activer dans les paramètres.") },
                confirmButton = {
                    Button(onClick = {
                        showGpsDialog = false
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }) {
                        Text("Paramètres")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showGpsDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }

        if (showBluetoothDialog) {
            AlertDialog(
                onDismissRequest = { showBluetoothDialog = false },
                title = { Text("Bluetooth désactivé") },
                text = { Text("Le Bluetooth est nécessaire pour se connecter à vos capteurs. Voulez-vous l'activer ?") },
                confirmButton = {
                    Button(onClick = {
                        showBluetoothDialog = false
                        bluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    }) {
                        Text("Activer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBluetoothDialog = false }) {
                        Text("Plus tard")
                    }
                }
            )
        }

        if (showStopDialog) {
            AlertDialog(
                onDismissRequest = { showStopDialog = false },
                title = { Text("Arrêter l'enregistrement") },
                text = { Text("Êtes-vous sûr de vouloir arrêter et sauvegarder cet enregistrement ?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showStopDialog = false
                            viewModel.stopRecording()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                    ) {
                        Text("Oui, arrêter")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStopDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}

/**
 * A single metric displayed in a black circle bubble.
 * Shows the value and unit text inside.
 */
@Composable
fun MetricBubble(
    value: String,
    unit: String,
    color: Color = Color.White,
    backgroundColor: Color = Color.Black.copy(alpha = 0.80f),
    progress: Float? = null,
    progressColor: Color? = null
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (progress != null && progressColor != null) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 6.dp.toPx()
                val inset = strokeWidth / 2
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth),
                    topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                    size = androidx.compose.ui.geometry.Size(size.width - strokeWidth, size.height - strokeWidth)
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-8).dp, Alignment.CenterVertically)
        ) {
            Text(
                text = value,
                color = color,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.padding(top = if (unit.isNotEmpty()) 6.dp else 0.dp)
            )
            if (unit.isNotEmpty()) {
                Text(
                    text = unit,
                    color = color.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

private fun formatTime(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) {
        "%d:%02d:%02d".format(h, m, s)
    } else {
        "%02d:%02d".format(m, s)
    }
}
