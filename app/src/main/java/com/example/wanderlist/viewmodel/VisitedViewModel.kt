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

class VisitedViewModel : ViewModel() {

    private val repository = DestinationRepository()

    private val _destinations = MutableLiveData<List<Destination>>()
    val destinations: LiveData<List<Destination>> = _destinations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadVisited()
    }

    fun loadVisited(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _isLoading.value = true
            val all = repository.getAllDestinations()
            _destinations.value = all.filter { it.visited }
            if (showLoading) _isLoading.value = false
        }
    }

    private fun isDuplicateLocation(lat: Double, lon: Double, excludeId: String? = null): Boolean {
        val threshold = 0.05
        return _destinations.value?.any {
            it.id != excludeId &&
                    (it.latitude != 0.0 || it.longitude != 0.0) &&
                    Math.abs(it.latitude - lat) < threshold &&
                    Math.abs(it.longitude - lon) < threshold
        } ?: false
    }

    fun addVisited(
        name: String,
        country: String,
        rating: Int,
        review: String,
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ) {
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

            val lat = coordinates?.lat ?: 0.0
            val lon = coordinates?.lon ?: 0.0

            if ((lat != 0.0 || lon != 0.0) && isDuplicateLocation(lat, lon)) {
                onResult(false, "Ova destinacija je već na popisu posjećenih.")
                return@launch
            }

            val destination = Destination(
                name = name,
                country = country,
                visited = true,
                rating = rating,
                notes = review,
                latitude = lat,
                longitude = lon
            )
            repository.addDestination(destination)
            loadVisited(showLoading = false)
            onResult(true, null)
        }
    }

    fun updateVisited(destination: Destination, name: String, country: String, rating: Int, review: String) {
        viewModelScope.launch {
            repository.updateDestination(
                destination.copy(name = name, country = country, rating = rating, notes = review)
            )
            loadVisited(showLoading = false)
        }
    }

    fun returnToWishlist(destination: Destination) {
        viewModelScope.launch {
            repository.updateDestination(destination.copy(visited = false))
            loadVisited(showLoading = false)
        }
    }

    fun deleteDestination(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteDestination(id)
            } catch (e: Exception) {
                android.util.Log.e("VisitedDebug", "Brisanje neuspješno: ${e.message}", e)
            }
            loadVisited(showLoading = false)
        }
    }
}