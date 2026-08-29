package com.example.wanderlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlist.data.model.Destination
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

    fun loadVisited() {
        viewModelScope.launch {
            _isLoading.value = true
            val all = repository.getAllDestinations()
            _destinations.value = all.filter { it.visited }
            _isLoading.value = false
        }
    }

    fun addVisited(name: String, country: String, rating: Int, review: String) {
        viewModelScope.launch {
            val destination = Destination(
                name = name,
                country = country,
                visited = true,
                rating = rating,
                notes = review
            )
            repository.addDestination(destination)
            loadVisited()
        }
    }

    fun updateVisited(destination: Destination, newRating: Int, newReview: String) {
        viewModelScope.launch {
            repository.updateDestination(destination.copy(rating = newRating, notes = newReview))
            loadVisited()
        }
    }

    fun deleteDestination(id: String) {
        viewModelScope.launch {
            repository.deleteDestination(id)
            loadVisited()
        }
    }

    fun returnToWishlist(destination: Destination) {
        viewModelScope.launch {
            repository.updateDestination(destination.copy(visited = false))
            loadVisited()
        }
    }
}