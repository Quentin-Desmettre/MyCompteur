package com.example.compteur.ui.dashboard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compteur.R
import com.example.compteur.domain.model.Route
import com.example.compteur.ui.components.MapComponent
import com.example.compteur.ui.theme.PureBlack
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    onRouteClick: (Long) -> Unit,
    onRunClick: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val routes by viewModel.routes.collectAsState()
    val selectedPoints by viewModel.selectedRoutePoints.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(routes) {
        if (routes.isNotEmpty() && viewModel.selectedRouteId.value == null) {
            viewModel.selectRoute(routes.first().id)
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(context.getString(it))
            viewModel.clearError()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importGpx(context, it) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                launcher.launch(arrayOf("*/*"))
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.import_gpx))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PureBlack
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(PureBlack)) {
            Text(
                text = stringResource(R.string.nav_dashboard),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

            if (routes.isEmpty()) {
                EmptyDashboard()
            } else {
                RoutesList(
                    routes, 
                    onRouteClick = { 
                        viewModel.selectRoute(it)
                        onRouteClick(it) 
                    }, 
                    onRunClick = onRunClick, 
                    onDelete = { viewModel.deleteRoute(it) }
                )
            }
        }
    }
}

@Composable
fun EmptyDashboard() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.no_routes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun RoutesList(routes: List<Route>, onRouteClick: (Long) -> Unit, onRunClick: (Long) -> Unit, onDelete: (Route) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(routes) { route ->
            RouteItem(route, onRouteClick, onRunClick, onDelete)
        }
    }
}

@Composable
fun RouteItem(route: Route, onRouteClick: (Long) -> Unit, onRunClick: (Long) -> Unit, onDelete: (Route) -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = { onRouteClick(route.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = route.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${"%.1f".format(route.distanceMeters / 1000f)} km - ${dateFormat.format(Date(route.dateImported))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = { onRunClick(route.id) }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = MaterialTheme.colorScheme.primary)
            }
            
            IconButton(onClick = { onDelete(route) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
