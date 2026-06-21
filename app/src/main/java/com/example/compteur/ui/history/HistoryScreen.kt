package com.example.compteur.ui.history

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
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
import com.example.compteur.domain.model.Session
import com.example.compteur.ui.theme.AccentCyan
import com.example.compteur.ui.theme.AccentRed
import com.example.compteur.ui.theme.PureBlack
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onSessionClick: (Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val sessions by viewModel.sessions.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportState.Success -> {
                try {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "com.example.compteur.fileprovider",
                        state.file
                    )
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/octet-stream" /* Or "application/fit" */
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Exporter la session FIT"))
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Erreur: \${e.message}")
                }
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
                title = { Text("Historique") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureBlack,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = PureBlack
    ) { innerPadding ->
        if (sessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Aucune session enregistrée", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sessions, key = { it.id }) { session ->
                    SessionItem(
                        session = session,
                        onClick = { onSessionClick(session.id) },
                        onExport = { viewModel.exportSessionToFit(session) },
                        onDelete = { viewModel.deleteSession(session) }
                    )
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
    }
}

@Composable
fun SessionItem(session: Session, onClick: () -> Unit, onExport: () -> Unit, onDelete: () -> Unit) {
    val formatter = SimpleDateFormat("dd MMM yyyy 'à' HH:mm", Locale.getDefault())
    val dateStr = formatter.format(Date(session.startedAt))
    val distanceKm = session.distanceMeters / 1000f
    
    val durationMs = (session.endedAt ?: System.currentTimeMillis()) - session.startedAt
    val durationH = durationMs / 3600000
    val durationM = (durationMs % 3600000) / 60000
    val durationStr = if (durationH > 0) "${durationH}h ${durationM}m" else "${durationM}m"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = dateStr, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Distance: %.1f km".format(distanceKm), color = Color.LightGray, fontSize = 14.sp)
                Text(text = "Durée: $durationStr", color = Color.LightGray, fontSize = 14.sp)
                Text(text = "D+ : %.0f m".format(session.ascentMeters), color = Color.LightGray, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = AccentRed)
                }
                IconButton(onClick = onExport) {
                    Icon(Icons.Default.Share, contentDescription = "Exporter", tint = AccentCyan)
                }
            }
        }
    }
}
