package com.example.wanderlist.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.sqrt

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    private val shakeThreshold = 7f
    private val requiredShakeCount = 2
    private val shakeTimeWindowMs = 600L
    private val minTimeBetweenShakes = 1500L

    private val recentShakeTimestamps = ArrayDeque<Long>()
    private var lastShakeTime = 0L

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gForce = sqrt(x * x + y * y + z * z) / android.hardware.SensorManager.GRAVITY_EARTH

        if (gForce > shakeThreshold) {
            android.util.Log.d("ShakeTest", "Skok: $gForce")
            val now = System.currentTimeMillis()
            recentShakeTimestamps.addLast(now)

            while (recentShakeTimestamps.isNotEmpty() && now - recentShakeTimestamps.first() > shakeTimeWindowMs) {
                recentShakeTimestamps.removeFirst()
            }

            if (recentShakeTimestamps.size >= requiredShakeCount && now - lastShakeTime > minTimeBetweenShakes) {
                lastShakeTime = now
                recentShakeTimestamps.clear()
                android.util.Log.d("ShakeTest", "TRESENJE DETEKTIRANO!")
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}