package com.example.aranyani3.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.aranyani3.MainActivity
import com.example.aranyani3.R

class WeatherNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID   = "weather_updates"
        const val CHANNEL_NAME = "Weather Updates"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    // Create the notification channel (Android 8+)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Hourly weather updates and alerts"
                enableLights(true)
                enableVibration(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showWeatherNotification(
        temperature: Double,
        weatherCode: Int,
        humidity: Int,
        windSpeed: Double
    ) {
        // Check permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val icon      = weatherEmoji(weatherCode)
        val desc      = weatherDesc(weatherCode)
        val isAlert   = weatherCode >= 65
        val title     = if (isAlert) "⚠️ Weather Alert" else "$icon Current Weather"
        val body      = "$desc · ${temperature.toInt()}°C · 💧$humidity% · 💨${windSpeed.toInt()} km/h"
        val priority  = if (isAlert) NotificationCompat.PRIORITY_HIGH
        else        NotificationCompat.PRIORITY_DEFAULT

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)      // add your icon to res/drawable
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun weatherEmoji(code: Int): String = when (code) {
        0 -> "☀️"; 1, 2 -> "🌤️"; 3 -> "☁️"
        45, 48 -> "🌫️"; 51, 53, 55 -> "🌦️"
        61, 63, 65 -> "🌧️"; 71, 73, 75, 77 -> "❄️"
        80, 81, 82 -> "⛈️"; 95, 96, 99 -> "🌩️"
        else -> "🌡️"
    }

    private fun weatherDesc(code: Int): String = when (code) {
        0 -> "Clear Sky"; 1 -> "Mainly Clear"; 2 -> "Partly Cloudy"; 3 -> "Overcast"
        45, 48 -> "Foggy"; 51, 53, 55 -> "Drizzle"
        61, 63 -> "Rain"; 65 -> "Heavy Rain"
        71, 73, 75 -> "Snow"; 80, 81, 82 -> "Showers"
        95 -> "Thunderstorm"; 96, 99 -> "Severe Storm"
        else -> "Unknown"
    }
}