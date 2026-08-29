package com.example.wanderlist.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val main: MainWeather,
    val weather: List<WeatherDescription>,
    val name: String
)

data class MainWeather(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    val humidity: Int
)

data class WeatherDescription(
    val description: String,
    val icon: String
)