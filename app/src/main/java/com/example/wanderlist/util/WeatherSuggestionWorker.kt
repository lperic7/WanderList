package com.example.wanderlist.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wanderlist.BuildConfig
import com.example.wanderlist.MainActivity
import com.example.wanderlist.R
import com.example.wanderlist.data.remote.RetrofitInstance
import com.example.wanderlist.data.repository.DestinationRepository

class WeatherSuggestionWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository = DestinationRepository()

    override suspend fun doWork(): Result {
        val wishlist = repository.getAllDestinations()
            .filter { !it.visited && (it.latitude != 0.0 || it.longitude != 0.0) }

        android.util.Log.d("WeatherWorker", "Broj destinacija za analizu: ${wishlist.size}")

        if (wishlist.isEmpty()) return Result.success()

        val idealTemp = 23.0
        var bestDestination: com.example.wanderlist.data.model.Destination? = null
        var bestScore = -Double.MAX_VALUE
        var bestAvgTemp = 0.0
        var bestDescription = ""

        for (destination in wishlist) {
            try {
                android.util.Log.d("WeatherWorker", "Šaljem zahtjev za ${destination.name}, lat=${destination.latitude}, lon=${destination.longitude}")

                val forecast = RetrofitInstance.weatherApi.getForecast(
                    lat = destination.latitude,
                    lon = destination.longitude,
                    apiKey = BuildConfig.OPEN_WEATHER_API_KEY
                )

                android.util.Log.d("WeatherWorker", "${destination.name}: forecast.list.size = ${forecast.list.size}")

                if (forecast.list.isEmpty()) continue

                val avgTemp = forecast.list.map { it.main.temp }.average()

                val itemScores = forecast.list.map { item ->
                    val description = item.weather.firstOrNull()?.description ?: ""
                    val conditionPenalty = when {
                        description.contains("kiša") || description.contains("oluja") -> -15.0
                        description.contains("snijeg") -> -20.0
                        description.contains("oblačno") -> -3.0
                        else -> 0.0
                    }
                    -Math.abs(item.main.temp - idealTemp) + conditionPenalty
                }

                val score = itemScores.average()

                val mostCommonDescription = forecast.list
                    .mapNotNull { it.weather.firstOrNull()?.description }
                    .groupingBy { it }
                    .eachCount()
                    .maxByOrNull { it.value }
                    ?.key ?: ""

                if (score > bestScore) {
                    bestScore = score
                    bestDestination = destination
                    bestAvgTemp = avgTemp
                    bestDescription = mostCommonDescription
                }
            } catch (e: Exception) {
                android.util.Log.e("WeatherWorker", "Greška za ${destination.name}: ${e.message}", e)
            }
        }

        android.util.Log.d("WeatherWorker", "Najbolja destinacija: ${bestDestination?.name}")

        bestDestination?.let { destination ->
            showNotification(destination.name, destination.country, bestAvgTemp.toInt(), bestDescription)
        }

        return Result.success()
    }

    private fun showNotification(name: String, country: String, temp: Int, description: String) {
        NotificationHelper.createNotificationChannel(applicationContext)

        val intent = android.content.Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentTitle("Idealno vrijeme za $name!")
            .setContentText("$temp°C, $description u $name, $country. Možda je vrijeme za posjet?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val hasPermission = ActivityCompat.checkSelfPermission(
            applicationContext, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        android.util.Log.d("WeatherWorker", "Ima dozvolu za notifikacije: $hasPermission")

        if (hasPermission) {
            NotificationManagerCompat.from(applicationContext)
                .notify(NotificationHelper.NOTIFICATION_ID, notification)
            android.util.Log.d("WeatherWorker", "Notifikacija poslana")
        }
    }
}