package com.kodex.guide.presentation.parallaxScreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import okhttp3.Request
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
fun MapScreen(
    destinationLat: Double,
    destinationLng: Double,
    destinationTitle: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var routePoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    var locationPermissionGranted by remember { mutableStateOf<Boolean?>(null) }
    var mapMessage by remember { mutableStateOf("") }

    remember(context) {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = context.filesDir
            osmdroidTileCache = context.cacheDir
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions.values.any { it }
    }

    LaunchedEffect(Unit) {
        val hasLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            locationPermissionGranted = true
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(locationPermissionGranted) {
        when (locationPermissionGranted) {
            true -> {
                userLocation = getUserLocation(context)
                if (userLocation == null) {
                    mapMessage = "Не удалось определить ваше местоположение."
                }
            }
            false -> {
                userLocation = null
                mapMessage = "Разрешите доступ к местоположению, чтобы построить маршрут."
            }
            null -> Unit
        }
    }

    LaunchedEffect(userLocation, destinationLat, destinationLng) {
        routePoints = emptyList()
        if (userLocation != null) {
            val start = userLocation!!
            routePoints = fetchRoute(
                startLat = start.latitude,
                startLng = start.longitude,
                endLat = destinationLat,
                endLng = destinationLng
            )
            if (routePoints.isEmpty()) {
                mapMessage = "Не удалось построить маршрут по дорогам."
            } else {
                mapMessage = ""
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text("Маршрут до: $destinationTitle")
                if (mapMessage.isNotEmpty()) {
                    Text(
                        text = mapMessage,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                AndroidView(
                    factory = ::createMapView,
                    update = { mapView ->
                        updateMapView(
                            mapView = mapView,
                            userLocation = userLocation,
                            destinationLat = destinationLat,
                            destinationLng = destinationLng,
                            destinationTitle = destinationTitle,
                            routePoints = routePoints
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                TextButton(onClick = onDismiss) {
                    Text("Закрыть")
                }
            }
        }
    }
}

private fun createMapView(context: Context): MapView =
    MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
        isClickable = true
        isFocusable = true
    }

private fun updateMapView(
    mapView: MapView,
    userLocation: GeoPoint?,
    destinationLat: Double,
    destinationLng: Double,
    destinationTitle: String,
    routePoints: List<GeoPoint>
) {
    mapView.overlays.clear()

    if (routePoints.isNotEmpty()) {
        val routeLine = Polyline().apply {
            setPoints(routePoints)
            outlinePaint.apply {
                color = Color.BLUE
                strokeWidth = 12f
                style = Paint.Style.STROKE
            }
        }
        mapView.overlays.add(routeLine)
    }

    val destinationMarker = Marker(mapView).apply {
        position = GeoPoint(destinationLat, destinationLng)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = destinationTitle
        setIcon(ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_mylocation))
    }
    mapView.overlays.add(destinationMarker)

    userLocation?.let { userPoint ->
        val userMarker = Marker(mapView).apply {
            position = userPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Вы здесь"
            setIcon(ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_myplaces))
        }
        mapView.overlays.add(userMarker)

        val bounds = BoundingBox.fromGeoPoints(listOf(userPoint, GeoPoint(destinationLat, destinationLng)))
        mapView.post { mapView.zoomToBoundingBox(bounds, true, 100) }
    } ?: run {
        mapView.controller.setCenter(GeoPoint(destinationLat, destinationLng))
        mapView.controller.setZoom(15.0)
    }

    mapView.invalidate()
}

private suspend fun getUserLocation(context: Context): GeoPoint? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val hasLocationPermission =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    if (!hasLocationPermission) return null

    for (provider in listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)) {
        try {
            if (!locationManager.isProviderEnabled(provider)) continue
            val lastKnownLocation = locationManager.getLastKnownLocation(provider)
            val location = lastKnownLocation ?: withTimeoutOrNull(12_000) {
                requestCurrentLocation(locationManager, provider, context)
            }
            if (location != null) return GeoPoint(location.latitude, location.longitude)
        } catch (error: CancellationException) {
            throw error
        } catch (error: SecurityException) {
            Log.e("MapScreen", "Location permission was revoked while reading $provider", error)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.e("MapScreen", "Unable to read location from $provider", error)
        }
    }
    return null
}

@androidx.annotation.RequiresPermission(anyOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
private suspend fun requestCurrentLocation(
    locationManager: LocationManager,
    provider: String,
    context: Context
): Location? = suspendCancellableCoroutine { continuation ->
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val cancellationSignal = CancellationSignal()
        continuation.invokeOnCancellation { cancellationSignal.cancel() }
        try {
            locationManager.getCurrentLocation(provider, cancellationSignal, context.mainExecutor) {
                location ->
                if (continuation.isActive) continuation.resume(location)
            }
        } catch (error: Exception) {
            if (continuation.isActive) continuation.resumeWithException(error)
        }
    } else {
        lateinit var listener: LocationListener
        listener = LocationListener { location ->
            if (continuation.isActive) {
                locationManager.removeUpdates(listener)
                continuation.resume(location)
            }
        }
        continuation.invokeOnCancellation { locationManager.removeUpdates(listener) }
        try {
            locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
        } catch (error: Exception) {
            if (continuation.isActive) continuation.resumeWithException(error)
        }
    }
}

private suspend fun fetchRoute(
    startLat: Double,
    startLng: Double,
    endLat: Double,
    endLng: Double
): List<GeoPoint> = withContext(Dispatchers.IO) {
    try {
        val url = "https://router.project-osrm.org/route/v1/driving/" +
            "$startLng,$startLat;$endLng,$endLat?overview=full&geometries=geojson"
        val request = Request.Builder().url(url).build()

        OkHttpClient().newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("MapScreen", "Route request failed with HTTP ${response.code}")
                return@withContext emptyList()
            }
            val responseBody = response.body?.string() ?: return@withContext emptyList()
            val routes = JsonParser.parseString(responseBody)
                .asJsonObject
                .getAsJsonArray("routes")
            if (routes.size() == 0) return@withContext emptyList()

            val coordinates = routes[0].asJsonObject
                .getAsJsonObject("geometry")
                .getAsJsonArray("coordinates")

            coordinates.map { coordinate ->
                val lng = coordinate.asJsonArray[0].asDouble
                val lat = coordinate.asJsonArray[1].asDouble
                GeoPoint(lat, lng)
            }
        }
    } catch (error: Exception) {
        Log.e("MapScreen", "Route request failed", error)
        emptyList()
    }
}
