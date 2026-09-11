package com.example.wanderlist.ui.map

import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.wanderlist.data.repository.DestinationRepository
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val repository = remember { DestinationRepository() }
    var count by remember { mutableIntStateOf(0) }
    val mapView = remember {
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        MapView(context).apply {
            setTileSource(TileSourceFactory.WIKIMEDIA)
            setMultiTouchControls(true)
            controller.setZoom(4.0)
            controller.setCenter(GeoPoint(48.8566, 2.3522))
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            val wishlist = repository.getAllDestinations()
                .filter { !it.visited && (it.latitude != 0.0 || it.longitude != 0.0) }

            count = wishlist.size
            val points = mutableListOf<GeoPoint>()

            mapView.overlays.clear()
            wishlist.forEach { destination ->
                val point = GeoPoint(destination.latitude, destination.longitude)
                points.add(point)

                val marker = Marker(mapView).apply {
                    position = point
                    title = destination.name
                    snippet = destination.country
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(marker)
            }

            if (points.isNotEmpty()) {
                val boundingBox = BoundingBox.fromGeoPoints(points)
                mapView.post {
                    mapView.zoomToBoundingBox(boundingBox, true, 100)
                }
            }
            mapView.invalidate()
        }

        onDispose { job.cancel() }
    }

    val countText = if (count == 0) "Nema destinacija s koordinatama za prikaz" else "$count destinacija na karti"

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(start = 16.dp, top = 16.dp),
        ) {
            Text(
                text = countText,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Dodan .clipToBounds() sprječava da se unutrašnji View širi izvan zadanih granica okvira
        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clipToBounds()
        )
    }
}