package com.example.wanderlist.data.model

data class Destination(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val country: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val notes: String = "",
    val visited: Boolean = false,
    val rating: Int = 0,
    val dateAdded: Long = System.currentTimeMillis()
)