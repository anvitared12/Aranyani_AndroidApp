package com.example.aranyani3.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.aranyani3.R

private const val TAG = "PlantReminder"
const val CHANNEL_ID = "plant_channel"

private const val PREFS_NAME            = "plant_reminder_prefs"

// Plant keys
private const val KEY_PLANT_INTERVAL_MS  = "interval_ms"
private const val KEY_PLANT_NEXT_TRIGGER = "next_trigger_ms"
private const val REQUEST_CODE_PLANT     = 1001

// Compost keys
private const val KEY_COMPOST_INTERVAL_MS  = "compost_interval_ms"
private const val KEY_COMPOST_NEXT_TRIGGER = "compost_next_trigger"
private const val REQUEST_CODE_COMP        = 1002

// Test
private const val REQUEST_CODE_TEST = 9999

// ── Single BroadcastReceiver handling all alarm types ─────────────────────────

class PlantReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val code = intent.getIntExtra("request_code", REQUEST_CODE_PLANT)
        Log.d(TAG, "Alarm received, code=$code")

        when (code) {
            REQUEST_CODE_TEST -> {
                // Test notification — just show it, don't reschedule
                showNotification(
                    context,
                    "Test 🌱",
                    "Notifications are working!",
                    REQUEST_CODE_TEST
                )
            }
            REQUEST_CODE_COMP -> {
                showNotification(
                    context,
                    "Turn your compost 🍂",
                    "Time to turn and aerate your compost pile!",
                    REQUEST_CODE_COMP
                )
                rescheduleCompost(context)
            }
            else -> {
                showNotification(
                    context,
                    "Water your plant 🌱",
                    "It's time to water your plant!",
                    REQUEST_CODE_PLANT
                )
                reschedulePlant(context)
            }
        }
    }
}

// ── Public API ────────────────────────────────────────────────────────────────

fun schedulePlantReminder(
    context: Context,
    startDateMillis: Long,
    intervalDays: Long
) {
    val intervalMs = intervalDays * 24 * 60 * 60 * 1000L
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        .putLong(KEY_PLANT_INTERVAL_MS, intervalMs)
        .putLong(KEY_PLANT_NEXT_TRIGGER, startDateMillis)
        .apply()
    setExactAlarm(context, startDateMillis, REQUEST_CODE_PLANT)
    Log.d(TAG, "Plant alarm set: first=$startDateMillis interval=${intervalDays}d")
}

fun cancelPlantReminder(context: Context) {
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    am.cancel(buildPendingIntent(context, REQUEST_CODE_PLANT))
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        .remove(KEY_PLANT_INTERVAL_MS)
        .remove(KEY_PLANT_NEXT_TRIGGER)
        .apply()
    Log.d(TAG, "Plant reminder cancelled")
}

fun scheduleCompostReminder(
    context: Context,
    startDateMillis: Long,
    intervalDays: Long
) {
    val intervalMs = intervalDays * 24 * 60 * 60 * 1000L
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        .putLong(KEY_COMPOST_INTERVAL_MS, intervalMs)
        .putLong(KEY_COMPOST_NEXT_TRIGGER, startDateMillis)
        .apply()
    setExactAlarm(context, startDateMillis, REQUEST_CODE_COMP)
    Log.d(TAG, "Compost alarm set: first=$startDateMillis interval=${intervalDays}d")
}

fun cancelCompostReminder(context: Context) {
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    am.cancel(buildPendingIntent(context, REQUEST_CODE_COMP))
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        .remove(KEY_COMPOST_INTERVAL_MS)
        .remove(KEY_COMPOST_NEXT_TRIGGER)
        .apply()
    Log.d(TAG, "Compost reminder cancelled")
}

fun scheduleTestNotification(context: Context) {
    val triggerMs = System.currentTimeMillis() + 10_000L
    setExactAlarm(context, triggerMs, REQUEST_CODE_TEST)
    Log.d(TAG, "Test alarm set for 10 seconds from now")
}

// ── Internal reschedule helpers ───────────────────────────────────────────────

private fun reschedulePlant(context: Context) {
    val prefs       = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val intervalMs  = prefs.getLong(KEY_PLANT_INTERVAL_MS, 0L)
    val lastTrigger = prefs.getLong(KEY_PLANT_NEXT_TRIGGER, 0L)
    if (intervalMs <= 0L || lastTrigger <= 0L) {
        Log.w(TAG, "Plant: no saved interval — not rescheduling")
        return
    }
    val next = lastTrigger + intervalMs
    prefs.edit().putLong(KEY_PLANT_NEXT_TRIGGER, next).apply()
    setExactAlarm(context, next, REQUEST_CODE_PLANT)
    Log.d(TAG, "Plant rescheduled for $next")
}

private fun rescheduleCompost(context: Context) {
    val prefs       = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val intervalMs  = prefs.getLong(KEY_COMPOST_INTERVAL_MS, 0L)
    val lastTrigger = prefs.getLong(KEY_COMPOST_NEXT_TRIGGER, 0L)
    if (intervalMs <= 0L || lastTrigger <= 0L) {
        Log.w(TAG, "Compost: no saved interval — not rescheduling")
        return
    }
    val next = lastTrigger + intervalMs
    prefs.edit().putLong(KEY_COMPOST_NEXT_TRIGGER, next).apply()
    setExactAlarm(context, next, REQUEST_CODE_COMP)
    Log.d(TAG, "Compost rescheduled for $next")
}

// ── Core alarm helpers ────────────────────────────────────────────────────────

private fun setExactAlarm(context: Context, triggerMs: Long, code: Int) {
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pi = buildPendingIntent(context, code)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
    } else {
        am.setExact(AlarmManager.RTC_WAKEUP, triggerMs, pi)
    }
}

private fun buildPendingIntent(context: Context, code: Int): PendingIntent {
    val intent = Intent(context, PlantReminderReceiver::class.java)
        .putExtra("request_code", code)
    return PendingIntent.getBroadcast(
        context, code, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

// ── Notification ──────────────────────────────────────────────────────────────

private fun showNotification(context: Context, title: String, body: String, id: Int) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    ensureChannelExists(manager)
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(body)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setAutoCancel(true)
        .build()
    manager.notify(id, notification)
}

fun ensureChannelExists(manager: NotificationManager) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Plant Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders to water your plants"
            enableLights(true)
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }
}