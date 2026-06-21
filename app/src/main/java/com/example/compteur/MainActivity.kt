package com.example.compteur

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compteur.ui.theme.CompteurTheme
import com.example.compteur.domain.repository.StravaRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.compteur.ui.dashboard.DashboardScreen
import com.example.compteur.ui.recording.RecordingScreen
import com.example.compteur.ui.route_detail.RouteDetailScreen
import com.example.compteur.ui.settings.SettingsScreen
import com.example.compteur.ui.history.HistoryScreen
import com.example.compteur.ui.session_detail.SessionDetailScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.fillMaxSize

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var stravaRepository: StravaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        setContent {
            CompteurTheme {
                MainScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data
        if (uri != null && uri.scheme == "compteur" && uri.host == "strava") {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                lifecycleScope.launch {
                    stravaRepository.authenticate(code)
                }
            }
        }
    }
}

sealed class Screen(val route: String, val resourceId: Int, val icon: @Composable () -> Unit) {
    object Dashboard : Screen("dashboard", R.string.nav_dashboard, { Icon(Icons.Default.Dashboard, contentDescription = null) })
    object Recording : Screen("recording", R.string.nav_recording, { Icon(Icons.Default.FiberManualRecord, contentDescription = null) })
    object History : Screen("history", R.string.nav_history, { Icon(Icons.Default.History, contentDescription = null) })
    object Settings : Screen("settings", R.string.nav_settings, { Icon(Icons.Default.Settings, contentDescription = null) })
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Dashboard, Screen.Recording, Screen.History, Screen.Settings)
    var isFullscreen by remember { mutableStateOf(false) }

    // Reset fullscreen when navigating away from recording
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    if (currentRoute != null && !currentRoute.startsWith("recording")) {
        isFullscreen = false
    }

    Scaffold(
        bottomBar = {
            if (!isFullscreen) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = screen.icon,
                            label = { Text(stringResource(screen.resourceId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Dashboard.route,
            modifier = if (isFullscreen) Modifier.fillMaxSize() else Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { 
                DashboardScreen(
                    onRouteClick = { routeId -> navController.navigate("route_detail/$routeId") },
                    onRunClick = { routeId -> navController.navigate("recording?routeId=$routeId") }
                ) 
            }
            composable(
                route = "recording?routeId={routeId}",
                arguments = listOf(navArgument("routeId") { 
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { 
                RecordingScreen(
                    onFullscreenChange = { isFullscreen = it }
                )
            }
            composable(
                route = "route_detail/{routeId}",
                arguments = listOf(navArgument("routeId") { type = NavType.LongType })
            ) { 
                RouteDetailScreen(
                    onBack = { navController.popBackStack() },
                    onRun = { routeId -> 
                        navController.navigate("recording?routeId=$routeId") {
                            popUpTo(Screen.Dashboard.route)
                        }
                    }
                ) 
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    onSessionClick = { sessionId -> navController.navigate("session_detail/$sessionId") }
                )
            }
            composable(
                route = "session_detail/{sessionId}",
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) {
                SessionDetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) { SettingsScreen(
                onNavigateToMapSelection = { navController.navigate("offline_map_selection") }
            ) }
            composable("offline_map_selection") {
                com.example.compteur.ui.settings.OfflineMapSelectionScreen(
                    onBack = { navController.popBackStack() },
                    onDownloadStarted = { navController.popBackStack() }
                )
            }
        }
    }
}
