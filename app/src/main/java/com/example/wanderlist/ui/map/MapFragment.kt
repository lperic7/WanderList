package com.example.wanderlist.ui.map

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.wanderlist.data.model.Destination
import com.example.wanderlist.data.repository.DestinationRepository
import com.example.wanderlist.databinding.FragmentMapBinding
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val repository = DestinationRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance().userAgentValue = requireContext().packageName
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.setTileSource(TileSourceFactory.WIKIMEDIA)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(4.0)
        binding.mapView.controller.setCenter(GeoPoint(48.8566, 2.3522))

        loadMarkers()
    }

    private fun loadMarkers() {
        viewLifecycleOwner.lifecycleScope.launch {
            val wishlist = repository.getAllDestinations()
                .filter { !it.visited && (it.latitude != 0.0 || it.longitude != 0.0) }

            binding.tvCount.text = when (wishlist.size) {
                0 -> "Nema destinacija s koordinatama za prikaz"
                1 -> "1 destinacija na karti"
                else -> "${wishlist.size} destinacija na karti"
            }

            val points = mutableListOf<GeoPoint>()

            wishlist.forEach { destination ->
                val point = GeoPoint(destination.latitude, destination.longitude)
                points.add(point)

                val marker = Marker(binding.mapView)
                marker.position = point
                marker.title = destination.name
                marker.snippet = destination.country
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                binding.mapView.overlays.add(marker)
            }

            if (points.isNotEmpty()) {
                val boundingBox = BoundingBox.fromGeoPoints(points)
                binding.mapView.post {
                    binding.mapView.zoomToBoundingBox(boundingBox, true, 100)
                }
            }

            binding.mapView.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}