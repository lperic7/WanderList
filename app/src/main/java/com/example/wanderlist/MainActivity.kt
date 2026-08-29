package com.example.wanderlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.wanderlist.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.wanderlist.util.WeatherSuggestionWorker
import java.util.concurrent.TimeUnit
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        requestNotificationPermissionIfNeeded()
        scheduleWeeklyWeatherCheck()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun scheduleWeeklyWeatherCheck() {
        //za testiranje: PeriodicWorkRequestBuilder<WeatherSuggestionWorker>(15, TimeUnit.MINUTES).build()
        val request = PeriodicWorkRequestBuilder<WeatherSuggestionWorker>(7, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "weekly_weather_check",
            androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }
}