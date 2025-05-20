package com.example.sawaapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.sawaapplication.screens.notification.ReminderNotificationService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SawaApp: Application() {

    override fun onCreate() {
        super.onCreate()

        // Create the channel so Android can show notifications
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Notification channels are only required on Android 8.0 (API 26+) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderNotificationService.REMINDER_CHANNEL_ID, // Channel ID
                "Event Notifications", // Channel name (shown to user)
                NotificationManager.IMPORTANCE_DEFAULT // Importance level
            ).apply {
                description = "Channel for Event Reminder"
            }

            // Register the channel with the system
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}