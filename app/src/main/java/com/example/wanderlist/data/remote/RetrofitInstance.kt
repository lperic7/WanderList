package com.example.wanderlist.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://api.openweathermap.org/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherApi: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }

    val geocodingApi: GeocodingApiService by lazy {
        retrofit.create(GeocodingApiService::class.java)
    }
}