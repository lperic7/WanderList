package com.example.wanderlist.ui.random

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.wanderlist.data.model.Destination
import com.example.wanderlist.data.repository.DestinationRepository
import com.example.wanderlist.databinding.FragmentRandomBinding
import com.example.wanderlist.util.ShakeDetector
import kotlinx.coroutines.launch

class RandomFragment : Fragment() {

    private var _binding: FragmentRandomBinding? = null
    private val binding get() = _binding!!

    private val repository = DestinationRepository()
    private var wishlist: List<Destination> = emptyList()

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var shakeDetector: ShakeDetector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnReshuffle.setOnClickListener { pickRandom() }

        sensorManager = requireContext().getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        shakeDetector = ShakeDetector { pickRandom() }

        loadWishlistAndPick()
    }

    private fun loadWishlistAndPick() {
        viewLifecycleOwner.lifecycleScope.launch {
            wishlist = repository.getAllDestinations().filter { !it.visited }
            pickRandom()
        }
    }

    private fun pickRandom() {
        if (wishlist.isEmpty()) {
            binding.cardResult.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.btnReshuffle.isEnabled = false
            return
        }

        binding.tvEmptyState.visibility = View.GONE
        binding.cardResult.visibility = View.VISIBLE
        binding.btnReshuffle.isEnabled = true

        val random = wishlist.random()
        binding.tvDestinationName.text = random.name
        binding.tvDestinationCountry.text = random.country
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(shakeDetector, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(shakeDetector)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}