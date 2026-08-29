package com.example.wanderlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlist.BuildConfig
import com.example.wanderlist.data.model.Destination
import com.example.wanderlist.data.remote.RetrofitInstance
import com.example.wanderlist.data.repository.DestinationRepository
import kotlinx.coroutines.launch

class WishlistViewModel : ViewModel() {

    private val repository = DestinationRepository()

    private val _destinations = MutableLiveData<List<Destination>>()
    val destinations: LiveData<List<Destination>> = _destinations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadDestinations()
    }

    fun loadDestinations() {
        viewModelScope.launch {
            _isLoading.value = true
            val all = repository.getAllDestinations()
            _destinations.value = all.filter { !it.visited }
            _isLoading.value = false
        }
    }

    fun addDestination(name: String, country: String) {
        viewModelScope.launch {
            val query = if (country.isNotBlank()) "$name,$country" else name

            val coordinates = try {
                RetrofitInstance.geocodingApi.getCoordinates(
                    query = query,
                    apiKey = BuildConfig.OPEN_WEATHER_API_KEY
                ).firstOrNull()
            } catch (e: Exception) {
                null
            }

            val destination = Destination(
                name = name,
                country = country,
                latitude = coordinates?.lat ?: 0.0,
                longitude = coordinates?.lon ?: 0.0
            )

            repository.addDestination(destination)
            loadDestinations()
        }
    }

    fun updateDestination(id: String, name: String, country: String) {
        viewModelScope.launch {
            val query = if (country.isNotBlank()) "$name,$country" else name

            val coordinates = try {
                RetrofitInstance.geocodingApi.getCoordinates(
                    query = query,
                    apiKey = BuildConfig.OPEN_WEATHER_API_KEY
                ).firstOrNull()
            } catch (e: Exception) {
                null
            }

            val existing = _destinations.value?.find { it.id == id }
            val updated = existing?.copy(
                name = name,
                country = country,
                latitude = coordinates?.lat ?: existing.latitude,
                longitude = coordinates?.lon ?: existing.longitude
            ) ?: return@launch

            repository.updateDestination(updated)
            loadDestinations()
        }
    }

    fun deleteDestination(id: String) {
        viewModelScope.launch {
            repository.deleteDestination(id)
            loadDestinations()
        }
    }

    fun markAsVisited(destination: Destination) {
        viewModelScope.launch {
            repository.updateDestination(destination.copy(visited = true))
            loadDestinations()
        }
    }
}