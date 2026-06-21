package com.example.compteur.ui.components

import android.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.compteur.domain.model.RoutePoint
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.example.compteur.R
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.layers.Property

@Composable
fun MapComponent(
    modifier: Modifier = Modifier,
    plannedPoints: List<RoutePoint>? = null,
    recordedPath: List<android.location.Location>? = null,
    currentLocation: android.location.Location? = null,
    mapStyleUrl: String = "https://tiles.openfreemap.org/styles/liberty",
    cameraMode: Int = 0,
    onCameraModeChange: ((Int) -> Unit)? = null,
    onMapReady: (() -> Unit)? = null
) {
    
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Keep a ref to the latest points so the style-ready callback can access them
    val pendingPointsRef = remember { mutableStateOf<List<RoutePoint>?>(null) }
    val pendingRecordedPathRef = remember { mutableStateOf<List<android.location.Location>?>(null) }
    // Track whether we've already fitted the camera for the *current* set of points
    var fittedPointsIdentity by remember { mutableStateOf<List<RoutePoint>?>(null) }
    // Track whether the style (and source/layer) is ready
    var styleReady by remember { mutableStateOf(false) }
    var currentStyleUrl by remember { mutableStateOf("") }
    
    // Track last applied list instances to prevent unnecessary GeoJSON updates
    val lastAppliedPlannedPointsRef = remember { mutableStateOf<List<RoutePoint>?>(null) }
    val lastAppliedRecordedPathRef = remember { mutableStateOf<List<android.location.Location>?>(null) }
    // Track previous camera mode to detect transitions and trigger auto-zoom
    val prevCameraModeRef = remember { intArrayOf(0) }

    // When points change, update the ref immediately
    pendingPointsRef.value = plannedPoints
    pendingRecordedPathRef.value = recordedPath

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    android.util.Log.d("MapComponent", "Lifecycle: onCreate")
                    mapView.onCreate(null)
                }
                Lifecycle.Event.ON_START -> {
                    android.util.Log.d("MapComponent", "Lifecycle: onStart")
                    mapView.onStart()
                }
                Lifecycle.Event.ON_RESUME -> {
                    android.util.Log.d("MapComponent", "Lifecycle: onResume")
                    mapView.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    android.util.Log.d("MapComponent", "Lifecycle: onPause")
                    mapView.onPause()
                }
                Lifecycle.Event.ON_STOP -> {
                    android.util.Log.d("MapComponent", "Lifecycle: onStop")
                    mapView.onStop()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    android.util.Log.d("MapComponent", "Lifecycle: onDestroy")
                    mapView.onDestroy()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Helper to apply points to the map source + fit camera
    fun applyPointsToMap(view: MapView, map: org.maplibre.android.maps.MapLibreMap, style: Style, points: List<RoutePoint>) {
        if (points.isEmpty()) return
        android.util.Log.d("MapComponent", "applyPointsToMap: ${points.size} points")
        
        // 1. Line Trace
        val latLngs = points.map { LatLng(it.latitude, it.longitude) }
        val lineString = LineString.fromLngLats(latLngs.map { Point.fromLngLat(it.longitude, it.latitude) })
        style.getSourceAs<GeoJsonSource>("planned-route-source")?.setGeoJson(Feature.fromGeometry(lineString))
        
        // 2. Start and End Points
        val startPoint = points.first()
        val endPoint = points.last()
        style.getSourceAs<GeoJsonSource>("planned-route-start-source")
            ?.setGeoJson(Feature.fromGeometry(Point.fromLngLat(startPoint.longitude, startPoint.latitude)))
        style.getSourceAs<GeoJsonSource>("planned-route-end-source")
            ?.setGeoJson(Feature.fromGeometry(Point.fromLngLat(endPoint.longitude, endPoint.latitude)))

        // 3. Direction Arrows: feed the line itself; the symbol layer places arrows
        // every N *pixels* along it (see setupPlannedRouteLayer), so the spacing stays
        // constant on screen at every zoom level instead of every X meters.
        style.getSourceAs<GeoJsonSource>("planned-route-arrows-source")
            ?.setGeoJson(Feature.fromGeometry(lineString))

        // Only fit camera if this is a new set of points
        val needsFit = fittedPointsIdentity !== points
        if (needsFit) {
            val fitCamera = {
                try {
                    val bounds = LatLngBounds.Builder().includes(latLngs).build()
                    val padX = (view.width * 0.1).toInt().coerceAtMost(100)
                    val padY = (view.height * 0.1).toInt().coerceAtMost(100)
                    android.util.Log.d("MapComponent", "Moving camera to bounds: $bounds")
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padX, padY, padX, padY))
                    fittedPointsIdentity = points
                } catch (e: Exception) {
                    android.util.Log.e("MapComponent", "Camera error: ${e.message}")
                }
            }

            if (view.width > 0 && view.height > 0) {
                fitCamera()
            } else {
                view.addOnLayoutChangeListener(object : android.view.View.OnLayoutChangeListener {
                    override fun onLayoutChange(v: android.view.View?, l: Int, t: Int, r: Int, b: Int, ol: Int, ot: Int, or_: Int, ob: Int) {
                        if (v != null && v.width > 0) {
                            v.removeOnLayoutChangeListener(this)
                            fitCamera()
                        }
                    }
                })
            }
        }
    }

    fun applyRecordedPathToMap(map: org.maplibre.android.maps.MapLibreMap, style: Style, pathPoints: List<android.location.Location>) {
        if (pathPoints.size < 2) return
        val latLngs = pathPoints.map { Point.fromLngLat(it.longitude, it.latitude) }
        val lineString = LineString.fromLngLats(latLngs)
        style.getSourceAs<GeoJsonSource>("recorded-route-source")?.setGeoJson(Feature.fromGeometry(lineString))
    }

    AndroidView(
        modifier = modifier,
        factory = {
            android.util.Log.d("MapComponent", "Factory: Returning MapView")
            mapView.apply {
                getMapAsync { map ->
                    android.util.Log.d("MapComponent", "Map ready, setting style: $mapStyleUrl")
                    currentStyleUrl = mapStyleUrl
                    map.setStyle(mapStyleUrl) { style ->
                        android.util.Log.d("MapComponent", "Style loaded successfully")
                        setupPlannedRouteLayer(style)
                        setupRecordedRouteLayer(style)
                        enableLocationComponent(context, map, style)
                        map.addOnCameraMoveStartedListener { reason ->
                            if (reason == org.maplibre.android.maps.MapLibreMap.OnCameraMoveStartedListener.REASON_API_GESTURE) {
                                onCameraModeChange?.invoke(0)
                            }
                        }
                        styleReady = true
                        onMapReady?.invoke()

                        val pending = pendingPointsRef.value
                        if (pending != null && pending.isNotEmpty()) {
                            android.util.Log.d("MapComponent", "Style ready: applying ${pending.size} pending points")
                            applyPointsToMap(this, map, style, pending)
                            lastAppliedPlannedPointsRef.value = pending
                        }
                        val pendingPath = pendingRecordedPathRef.value
                        if (pendingPath != null && pendingPath.isNotEmpty()) {
                            applyRecordedPathToMap(map, style, pendingPath)
                            lastAppliedRecordedPathRef.value = pendingPath
                        }
                    }
                }
            }
        },
        update = { view ->
            view.getMapAsync { map ->
                if (currentStyleUrl != mapStyleUrl) {
                    android.util.Log.d("MapComponent", "Style changed to: $mapStyleUrl")
                    currentStyleUrl = mapStyleUrl
                    styleReady = false
                    map.setStyle(mapStyleUrl) { style ->
                        android.util.Log.d("MapComponent", "New style loaded successfully")
                        setupPlannedRouteLayer(style)
                        setupRecordedRouteLayer(style)
                        enableLocationComponent(view.context, map, style)
                        styleReady = true
                        onMapReady?.invoke()

                        val pending = plannedPoints ?: pendingPointsRef.value
                        if (pending != null && pending.isNotEmpty()) {
                            applyPointsToMap(view, map, style, pending)
                            lastAppliedPlannedPointsRef.value = pending
                        }
                        val pendingPath = recordedPath ?: pendingRecordedPathRef.value
                        if (pendingPath != null && pendingPath.isNotEmpty()) {
                            applyRecordedPathToMap(map, style, pendingPath)
                            lastAppliedRecordedPathRef.value = pendingPath
                        }
                    }
                    return@getMapAsync
                }

                android.util.Log.d("MapComponent", "Update triggered. points: ${plannedPoints?.size ?: "null"}, styleReady: $styleReady")
                if (!styleReady) {
                    // Points will be applied in the style-ready callback above
                    return@getMapAsync
                }
                val style = map.style
                if (style == null) {
                    android.util.Log.w("MapComponent", "Update: Style is null despite styleReady flag")
                    return@getMapAsync
                }
                
                if (plannedPoints != null && plannedPoints.isNotEmpty()) {
                    if (plannedPoints !== lastAppliedPlannedPointsRef.value) {
                        applyPointsToMap(view, map, style, plannedPoints)
                        lastAppliedPlannedPointsRef.value = plannedPoints
                    }
                } 
                
                if (recordedPath != null && recordedPath.isNotEmpty()) {
                    if (recordedPath !== lastAppliedRecordedPathRef.value) {
                        applyRecordedPathToMap(map, style, recordedPath)
                        lastAppliedRecordedPathRef.value = recordedPath
                    }
                }

                // Auto-zoom to navigation level when entering tracking mode.
                // Done BEFORE setting the LocationComponent camera mode to avoid conflicts
                // between our zoom animation and the tracking pan animation.
                val justStartedTracking = cameraMode != 0 && prevCameraModeRef[0] == 0
                if (justStartedTracking) {
                    val cameraUpdate = if (currentLocation != null) {
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(currentLocation.latitude, currentLocation.longitude),
                            NAV_ZOOM
                        )
                    } else {
                        CameraUpdateFactory.zoomTo(NAV_ZOOM)
                    }
                    map.animateCamera(cameraUpdate, 600, null)
                }
                prevCameraModeRef[0] = cameraMode

                // Update camera tracking mode
                try {
                    val locationComponent = map.locationComponent
                    if (locationComponent.isLocationComponentEnabled) {
                        when (cameraMode) {
                            1 -> {
                                locationComponent.cameraMode = CameraMode.TRACKING
                                locationComponent.renderMode = RenderMode.NORMAL
                            }
                            2 -> {
                                locationComponent.cameraMode = CameraMode.TRACKING_COMPASS
                                locationComponent.renderMode = RenderMode.COMPASS
                            }
                            else -> {
                                locationComponent.cameraMode = CameraMode.NONE
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MapComponent", "Error updating camera mode: ${e.message}")
                }
                
                // Only force camera position when tracking is disabled and no tracked route
                if (cameraMode == 0 && currentLocation != null && (plannedPoints == null || plannedPoints.isEmpty())) {
                    val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                    android.util.Log.d("MapComponent", "Centering on current location: $latLng")
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0))
                }
            }
        }
    )
}

private fun setupRecordedRouteLayer(style: Style) {
    if (style.getSource("recorded-route-source") == null) {
        val source = GeoJsonSource("recorded-route-source")
        style.addSource(source)
        
        val layer = LineLayer("recorded-route-layer", "recorded-route-source")
        layer.setProperties(
            PropertyFactory.lineColor(Color.parseColor("#FF0000")),
            PropertyFactory.lineWidth(5f),
            PropertyFactory.lineOpacity(0.8f)
        )
        style.addLayer(layer)
    }
}

/** Spacing between direction arrows along the route, in screen pixels. */
private const val ARROW_SPACING_PX = 120f

/**
 * Zoom level applied automatically when entering tracking mode.
 * At ~17, roughly 300–400 m are visible: enough to anticipate turns
 * without losing street-level precision.
 */
private const val NAV_ZOOM = 17.0

private fun setupPlannedRouteLayer(style: Style) {
    if (style.getSource("planned-route-source") == null) {
        val source = GeoJsonSource("planned-route-source")
        style.addSource(source)
        
        val layer = LineLayer("planned-route-layer", "planned-route-source")
        layer.setProperties(
            PropertyFactory.lineColor(Color.parseColor("#0000FF")),
            PropertyFactory.lineWidth(5f),
            PropertyFactory.lineOpacity(0.7f)
        )
        style.addLayer(layer)
    }

    // Add icons to style if not already present
    val context = org.maplibre.android.MapLibre.getApplicationContext()
    if (style.getImage("start-icon") == null) {
        drawableToBitmap(context, R.drawable.ic_flag_start)?.let { style.addImage("start-icon", it) }
    }
    if (style.getImage("end-icon") == null) {
        drawableToBitmap(context, R.drawable.ic_flag_end)?.let { style.addImage("end-icon", it) }
    }
    if (style.getImage("arrow-icon") == null) {
        drawableToBitmap(context, R.drawable.ic_arrow_blue)?.let { style.addImage("arrow-icon", it) }
    }

    // Setup sources and layers for markers and arrows
    if (style.getSource("planned-route-start-source") == null) {
        style.addSource(GeoJsonSource("planned-route-start-source"))
        val startLayer = SymbolLayer("planned-route-start-layer", "planned-route-start-source")
        startLayer.setProperties(
            PropertyFactory.iconImage("start-icon"),
            PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM_LEFT),
            PropertyFactory.iconAllowOverlap(true)
        )
        style.addLayer(startLayer)
    }

    if (style.getSource("planned-route-end-source") == null) {
        style.addSource(GeoJsonSource("planned-route-end-source"))
        val endLayer = SymbolLayer("planned-route-end-layer", "planned-route-end-source")
        endLayer.setProperties(
            PropertyFactory.iconImage("end-icon"),
            PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM_LEFT),
            PropertyFactory.iconAllowOverlap(true)
        )
        style.addLayer(endLayer)
    }

    if (style.getSource("planned-route-arrows-source") == null) {
        style.addSource(GeoJsonSource("planned-route-arrows-source"))
        val arrowsLayer = SymbolLayer("planned-route-arrows-layer", "planned-route-arrows-source")
        arrowsLayer.setProperties(
            PropertyFactory.iconImage("arrow-icon"),
            // Place arrows along the line, spaced by pixels (not meters): MapLibre
            // re-lays them out on every zoom, so we keep a roughly constant on-screen
            // density. Avoids arrows piling up when zoomed out / disappearing when zoomed in.
            PropertyFactory.symbolPlacement(Property.SYMBOL_PLACEMENT_LINE),
            PropertyFactory.symbolSpacing(ARROW_SPACING_PX),
            // The arrow bitmap points "up"; with line placement MapLibre rotates it to
            // the line's screen angle (east = 0), so +90° makes it follow the travel
            // direction. iconKeepUpright(false) keeps it from flipping on backward segments.
            PropertyFactory.iconRotate(90f),
            PropertyFactory.iconKeepUpright(false),
            PropertyFactory.iconRotationAlignment(Property.ICON_ROTATION_ALIGNMENT_MAP),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true),
            PropertyFactory.iconSize(0.8f)
        )
        style.addLayer(arrowsLayer)
    }
}

private fun drawableToBitmap(context: android.content.Context, drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

private fun enableLocationComponent(context: android.content.Context, map: org.maplibre.android.maps.MapLibreMap, style: Style) {
    try {
        val locationComponent = map.locationComponent
        val activationOptions = LocationComponentActivationOptions.builder(context, style)
            .useDefaultLocationEngine(true)
            .build()
        locationComponent.activateLocationComponent(activationOptions)
        locationComponent.isLocationComponentEnabled = true
        locationComponent.renderMode = RenderMode.COMPASS
        locationComponent.cameraMode = CameraMode.NONE
        android.util.Log.d("MapComponent", "Location component enabled")
    } catch (e: Exception) {
        android.util.Log.e("MapComponent", "Failed to enable location component: ${e.message}")
    }
}
