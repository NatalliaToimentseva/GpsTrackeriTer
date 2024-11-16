package com.iTergt.routgpstracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.ui.MainActivity

const val CHANNEL_ID = "channel_1"
private const val NOTIFICATION_CHANNEL_NAME = "location_service_channel"
private const val NOTIFICATION_TITLE = "Gps tracker is running..."

/**
 * NotificationManager is responsible for managing notifications related to the location service.
 * It creates a notification channel and builds notifications to inform the user about the
 * status of the GPS tracking service.
 *
 * @property context The context in which the NotificationManager is operating. This is typically
 *                   the application context.
 *
 * @constructor Initializes the NotificationManager, creating the notification channel upon
 *              instantiation.
 */
class NotificationManager(private val context: Context) {

    // The NotificationManager instance used to create and manage notification channels.
    private val notificationManager =
        context.getSystemService(NotificationManager::class.java) as NotificationManager

    /**
     * Initializes the NotificationManager by creating the notification channel.
     * This is called automatically when an instance of NotificationManager is created.
     */
    init {
        createNotificationChannel()
    }

    /**
     * Builds and returns a notification for the location service.
     *
     * @return A Notification object configured with a small icon, title, and a pending intent
     *         that opens the MainActivity when the notification is tapped.
     */
    fun buildNotification(): Notification {
        return Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentIntent(
                PendingIntent.getActivity(
                    context, 0,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()
    }

    /**
     * Creates a notification channel for the location service notifications.
     * This method is called during the initialization of the NotificationManager.
     * It sets the channel ID, name, and importance level for the notifications.
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }
}