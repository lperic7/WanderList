package com.example.wanderlist.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {

    //pretvorba imena grada u koordinate
    @GET("geo/1.0/direct")
    suspend fun getCoordinates(
        @Query("q") query: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<GeocodingResult>
}

data class GeocodingResult(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)