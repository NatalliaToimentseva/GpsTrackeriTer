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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.iTergt.routgpstracker.controllers.LocationController
import com.iTergt.routgpstracker.models.LocationModel
import org.koin.android.ext.android.inject
import org.osmdroid.util.GeoPoint

private const val NOTIFICATION_ID = 1
private const val FORMAT = "%.1f"
private const val MLS_IN_SEC = 1000.0f
private const val COEF_TO_KM_H = 3.6f
private const val START_DISTANCE = 0.0f

class LocationService : Service() {

    private val notificationManager: NotificationManager by inject()
    private val locationController: LocationController by inject()
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null
    private var distance = START_DISTANCE
    private val geoPoints: ArrayList<GeoPoint> = arrayListOf()

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
        startTime = 0L
        locationCallback?.let { fusedLocationProvider?.removeLocationUpdates(it) }
    }

    private fun initLocation() {
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(5000)
            .build()
        locationCallback = object : LocationCallback() {

            @SuppressLint("DefaultLocale")
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                if (lastLocation != null) {
                    result.lastLocation?.let { location ->
//                        if(location.speed > 0.2) {
                        lastLocation?.run {
                            distance += distanceTo(location)
                        }
                        geoPoints.add(GeoPoint(location.latitude, location.longitude))
                        val distanceFormated = String.format(FORMAT, distance)
                        val speedFormated = String.format(FORMAT, COEF_TO_KM_H * location.speed)
                        val averageSpeedFormated = String.format(
                            FORMAT,
                            COEF_TO_KM_H * (distance / ((System.currentTimeMillis() - LocationService.startTime) / MLS_IN_SEC))
                        )
                        val locationModel = LocationModel(
                            speedFormated,
                            averageSpeedFormated,
                            distanceFormated,
                            geoPoints
                        )
                        locationController.locationData.onNext(locationModel)
//                    }
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
        var startTime = 0L
    }
}
