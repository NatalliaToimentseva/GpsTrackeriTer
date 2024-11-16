package com.iTergt.routgpstracker.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.iTergt.routgpstracker.controllers.LocationController
import com.iTergt.routgpstracker.models.LocationModel
import com.iTergt.routgpstracker.ui.settings.DEFAULT_UPDATE_TIME
import com.iTergt.routgpstracker.ui.settings.TIME_PREFERENCE_KEY
import org.koin.android.ext.android.inject
import org.osmdroid.util.GeoPoint

private const val NOTIFICATION_ID = 1
private const val FORMAT = "%.1f"
private const val MLS_IN_SEC = 1000.0f
private const val COEF_TO_KM_H = 3.6f
private const val START_DISTANCE = 0.0f
private const val START_TIME = 0L
private const val DEFAULT_UPDATE_TIME_LONG = 3000L

class LocationService : Service() {

    private val notificationManager: NotificationManager by inject()
    private val locationController: LocationController by inject()
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null
    private var distance = START_DISTANCE
    private val geoPoints: ArrayList<GeoPoint> = arrayListOf()

    private var isDebug = true //for debug only

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        initLocation()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            notificationManager.buildNotification()
        )
        startLocationUpdates()
        isRunning = true
        startTime = System.currentTimeMillis()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        startTime = START_TIME
        locationCallback?.let { fusedLocationProvider?.removeLocationUpdates(it) }
    }

    private fun initLocation() {
        val locationUpdateInterval = PreferenceManager.getDefaultSharedPreferences(this).getString(
            TIME_PREFERENCE_KEY, DEFAULT_UPDATE_TIME
        )?.toLongOrNull() ?: DEFAULT_UPDATE_TIME_LONG
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, locationUpdateInterval)
            .setMinUpdateIntervalMillis(locationUpdateInterval)
            .build()
        locationCallback = object : LocationCallback() {

            @SuppressLint("DefaultLocale")
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                if (lastLocation != null) {
                    result.lastLocation?.let { location ->
                        if (location.speed > 0.5 || isDebug) {
                            lastLocation?.run {
                                distance += distanceTo(location)
                            }
                            geoPoints.add(GeoPoint(location.latitude, location.longitude))
                            val distanceFormated = String.format(FORMAT, distance)
                            val speedFormated = String.format(FORMAT, COEF_TO_KM_H * location.speed)
                            val averageSpeedFormated = String.format(
                                FORMAT,
                                COEF_TO_KM_H * (distance / ((System.currentTimeMillis() - startTime) / MLS_IN_SEC))
                            )
                            val locationModel = LocationModel(
                                speedFormated,
                                averageSpeedFormated,
                                distanceFormated,
                                geoPoints
                            )
                            locationController.locationData.onNext(locationModel)
                        }
                    }
                }
                lastLocation = result.lastLocation
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        if (locationRequest != null && locationCallback != null) {
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest!!,
                locationCallback!!,
                Looper.getMainLooper()
            )
        }
    }

    companion object {
        var isRunning = false
        var startTime = START_TIME
    }
}
