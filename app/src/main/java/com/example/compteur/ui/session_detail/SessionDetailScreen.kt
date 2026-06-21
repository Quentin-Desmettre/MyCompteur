package com.example.compteur.ui.session_detail

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compteur.ui.components.MapComponent
import com.example.compteur.ui.theme.AccentCyan
import com.example.compteur.ui.theme.PureBlack
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    onBack: () -> Unit,
    viewModel: SessionDetailViewModel = hiltViewModel()
) {
    val session by viewModel.session.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val isStravaConnected by viewModel.isStravaConnected.collectAsState(initial = false)

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showStravaDialog by remember { mutableStateOf(false) }

    val formatter = remember { SimpleDateFormat("dd MMM yyyy 'à' HH:mm", Locale.getDefault()) }

    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportState.SuccessDownload -> {
                try {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "com.example.compteur.fileprovider",
                        state.file
                    )
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/octet-stream"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Télécharger la session FIT"))
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Erreur: ${e.message}")
                }
                viewModel.resetExportState()
            }
            is ExportState.RequireStravaLogin -> {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW, 
                        android.net.Uri.parse("${com.example.compteur.utils.StravaConstants.AUTH_URL}?client_id=${com.example.compteur.utils.StravaConstants.CLIENT_ID}&response_type=code&redirect_uri=${com.example.compteur.utils.StravaConstants.REDIRECT_URI}&approval_prompt=force&scope=${com.example.compteur.utils.StravaConstants.SCOPE}")
                    )
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    snackbarHostState.showSnackbar("Aucun navigateur trouvé")
                }
                viewModel.resetExportState()
            }
            is ExportState.SuccessStravaUpload -> {
                snackbarHostState.showSnackbar("Activité envoyée sur Strava avec succès !")
                viewModel.resetExportState()
            }
            is ExportState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetExportState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(session?.startedAt?.let { formatter.format(Date(it)) } ?: "", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureBlack)
            )
        },
        containerColor = PureBlack
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Map
            MapComponent(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                plannedPoints = routePoints
            )

            // Statistics Grid
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    session?.let { s ->
                        val durationMs = (s.endedAt ?: System.currentTimeMillis()) - s.startedAt
                        val durationH = durationMs / 3600000
                        val durationM = (durationMs % 3600000) / 60000
                        val durationStr = if (durationH > 0) "${durationH}h ${durationM}m" else "${durationM}m"

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                title = "Distance",
                                value = "%.1f".format(s.distanceMeters / 1000f),
                                unit = "km",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Durée",
                                value = durationStr,
                                unit = "",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                title = "Vitesse Moy.",
                                value = "%.1f".format(s.avgSpeedKph),
                                unit = "km/h",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Vitesse Max.",
                                value = "%.1f".format(s.maxSpeedKph),
                                unit = "km/h",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                title = "Dénivelé",
                                value = "%.0f".format(s.ascentMeters),
                                unit = "m",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "FC Moyenne",
                                value = s.avgHeartRateBpm?.toString() ?: "--",
                                unit = "bpm",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                title = "Puissance Moy.",
                                value = s.avgPowerWatts?.toString() ?: "--",
                                unit = "W",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.exportForDownload() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                        ) {
                            Text("Télécharger le fichier .fit", color = PureBlack, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { 
                                if (isStravaConnected) {
                                    showStravaDialog = true
                                } else {
                                    viewModel.exportForStrava()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFC4C02))
                        ) {
                            Text("Exporter vers Strava", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        if (exportState is ExportState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentCyan)
            }
        }
        
        if (showStravaDialog) {
            StravaConfigDialog(
                onDismiss = { showStravaDialog = false },
                onConfirm = { name, description, isCommute, isTrainer, isPrivate ->
                    showStravaDialog = false
                    viewModel.exportForStrava(
                        name = name,
                        description = description,
                        isCommute = isCommute,
                        isTrainer = isTrainer,
                        isPrivate = isPrivate
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StravaConfigDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, isCommute: Boolean, isTrainer: Boolean, isPrivate: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("Activité Vélo") }
    var description by remember { mutableStateOf("Enregistré avec MyCompteur") }
    var isCommute by remember { mutableStateOf(false) }
    var isTrainer by remember { mutableStateOf(false) }
    var isPrivate by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Paramètres Strava") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de l'activité") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isCommute,
                        onCheckedChange = { isCommute = it }
                    )
                    Text("Trajet Vélotaf")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isTrainer,
                        onCheckedChange = { isTrainer = it }
                    )
                    Text("Home Trainer")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it }
                    )
                    Text("Masquer du fil d'actualité (Privé)")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, description, isCommute, isTrainer, isPrivate) }
            ) {
                Text("Uploader", color = Color(0xFFFC4C02), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = Color.Gray)
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
fun StatCard(title: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}
