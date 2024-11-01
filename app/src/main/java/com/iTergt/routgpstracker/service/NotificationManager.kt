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

class NotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(NotificationManager::class.java) as NotificationManager

    init {
        createNotificationChannel()
    }

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

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}