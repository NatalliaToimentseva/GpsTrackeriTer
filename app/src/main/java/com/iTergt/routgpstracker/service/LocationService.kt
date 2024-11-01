package com.iTergt.routgpstracker.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.koin.android.ext.android.inject

private const val NOTIFICATION_ID = 1

class LocationService : Service() {

    private val notificationManager: NotificationManager by inject()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            notificationManager.buildNotification()
        )
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}