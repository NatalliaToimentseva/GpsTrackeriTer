package com.iTergt.routgpstracker.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.koin.android.ext.android.inject

private const val NOTIFICATION_ID = 1

class LocationService : Service() {

    private val notificationManager: NotificationManager by inject()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            notificationManager.buildNotification()
        )
        isRunning = true
        startTime = System.currentTimeMillis()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        startTime = 0L
    }

    companion object {
        var isRunning = false
        var startTime = 0L
    }
}