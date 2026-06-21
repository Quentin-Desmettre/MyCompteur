package com.example.compteur.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compteur.ui.components.MapSelectionComponent
import com.example.compteur.ui.theme.PureBlack
import org.maplibre.android.geometry.LatLngBounds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineMapSelectionScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onDownloadStarted: () -> Unit
) {
    val lastKnownLocation by viewModel.lastKnownLocation.collectAsState()
    val currentMapStyle by viewModel.currentMapStyle.collectAsState()
    
    var selectionBounds by remember { mutableStateOf<LatLngBounds?>(null) }
    var regionName by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sélectionner la zone", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureBlack)
            )
        },
        containerColor = PureBlack
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // The Map Selector
            MapSelectionComponent(
                modifier = Modifier.fillMaxSize(),
                mapStyleUrl = currentMapStyle.url,
                initialCenter = lastKnownLocation,
                onBoundsChanged = { selectionBounds = it }
            )

            // UI Overlay (Bottom Card)
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nom de la région",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                    OutlinedTextField(
                        value = regionName,
                        onValueChange = { regionName = it },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        placeholder = { Text("Ex: Ma ville, Alpes...", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true
                    )
                    
                    Button(
                        onClick = {
                            selectionBounds?.let { bounds ->
                                val name = if (regionName.isNotBlank()) regionName else "Zone ${System.currentTimeMillis() % 10000}"
                                viewModel.downloadRegion(name, bounds)
                                onDownloadStarted()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectionBounds != null,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Télécharger cette zone", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
