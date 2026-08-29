package com.example.wanderlist.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    //za prikaz na kartici
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "hr"
    ): WeatherResponse

    //za notifikaciju
    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "hr"
    ): ForecastResponse
}