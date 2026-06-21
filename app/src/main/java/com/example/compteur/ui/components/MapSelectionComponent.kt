package com.example.compteur.ui.components

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style

@Composable
fun MapSelectionComponent(
    modifier: Modifier = Modifier,
    mapStyleUrl: String = "https://tiles.openfreemap.org/styles/liberty",
    initialCenter: LatLng? = null,
    onBoundsChanged: (LatLngBounds) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                mapView.apply {
                    getMapAsync { map ->
                        mapLibreMap = map
                        map.setStyle(mapStyleUrl)
                        initialCenter?.let {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 12.0))
                        }
                        
                        map.addOnCameraIdleListener {
                            calculateBounds(map, mapView.width, mapView.height, onBoundsChanged)
                        }
                    }
                }
            },
            update = { view ->
                // Handle style updates if needed
            }
        )

        // Viewfinder Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Dimensions of the selection rectangle (80% width, adjusted height)
            val rectWidth = canvasWidth * 0.8f
            val rectHeight = canvasHeight * 0.5f
            val left = (canvasWidth - rectWidth) / 2
            val top = (canvasHeight - rectHeight) / 2

            with(drawContext.canvas.nativeCanvas) {
                val checkpoint = saveLayer(null, null)

                // 1. Draw the semi-transparent overlay
                drawRect(color = Color.Black.copy(alpha = 0.5f))

                // 2. Clear the center rectangle
                drawRect(
                    color = Color.Transparent,
                    topLeft = Offset(left, top),
                    size = Size(rectWidth, rectHeight),
                    blendMode = BlendMode.Clear
                )

                restoreToCount(checkpoint)
            }

            // 3. Draw a border around the cleared area
            drawRect(
                color = Color.White,
                topLeft = Offset(left, top),
                size = Size(rectWidth, rectHeight),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

private fun calculateBounds(
    map: MapLibreMap,
    viewWidth: Int,
    viewHeight: Int,
    onBoundsChanged: (LatLngBounds) -> Unit
) {
    if (viewWidth <= 0 || viewHeight <= 0) return

    val rectWidth = viewWidth * 0.8f
    val rectHeight = viewHeight * 0.5f
    val left = (viewWidth - rectWidth) / 2
    val top = (viewHeight - rectHeight) / 2
    val right = left + rectWidth
    val bottom = top + rectHeight

    val northEast = map.projection.fromScreenLocation(PointF(right, top))
    val southWest = map.projection.fromScreenLocation(PointF(left, bottom))

    val bounds = LatLngBounds.Builder()
        .include(northEast)
        .include(southWest)
        .build()

    onBoundsChanged(bounds)
}
