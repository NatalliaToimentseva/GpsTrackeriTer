package com.iTergt.routgpstracker.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
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

class LocationService : Service() {

    private val notificationManager: NotificationManager by inject()
    private val locationController: LocationController by inject()
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null
    private var distance = 0.0f
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

            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                if (lastLocation != null) {
                    result.lastLocation?.let { location ->
//                        if(location.speed > 0.2) {
                        lastLocation?.run {
                            distance += distanceTo(location)
                        }
                        geoPoints.add(GeoPoint(location.latitude, location.longitude))
                        val locationModel = LocationModel(
                            location.speed,
                            distance,
                            geoPoints
                        )
                        locationController.locationData.onNext(locationModel)
//                    }
                    }
                }
                lastLocation = result.lastLocation
                Log.d("AAA", "Distance: $distance")
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
