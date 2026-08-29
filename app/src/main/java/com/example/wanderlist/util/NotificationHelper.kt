package com.example.wanderlist.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationHelper {

    const val CHANNEL_ID = "weather_suggestion_channel"
    const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Prijedlozi destinacija",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Tjedni prijedlog destinacije na temelju vremenske prognoze"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}