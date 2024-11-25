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
import com.iTergt.routgpstracker.ui.tabs.settings.DEFAULT_UPDATE_TIME
import com.iTergt.routgpstracker.ui.tabs.settings.TIME_PREFERENCE_KEY
import org.koin.android.ext.android.inject
import org.osmdroid.util.GeoPoint

private const val NOTIFICATION_ID = 1
private const val FORMAT = "%.1f"
private const val MLS_IN_SEC = 1000.0f
private const val COEF_TO_KM_H = 3.6f
private const val START_DISTANCE = 0.0f
private const val START_TIME = 0L
private const val DEFAULT_UPDATE_TIME_LONG = 3000L
private const val MIN_SPEED_TO_DETECT_MOVING = 0.5

/**
 * A service that provides continuous location updates in the foreground.
 *
 * This service utilizes the Fused Location Provider to obtain location data
 * and broadcasts it through a LocationController. It runs in the foreground
 * to ensure that location updates continue even when the app is not in the foreground.
 */
class LocationService : Service() {

    private val notificationManager: NotificationManager by inject() // Notification manager for foreground service
    private val locationController: LocationController by inject() // Controller to manage location data'
    private val geoPoints: ArrayList<GeoPoint> = arrayListOf() // List of geographical points
    private var fusedLocationProvider: FusedLocationProviderClient? =
        null // Fused location provider client
    private var locationRequest: LocationRequest? = null // Location request configuration
    private var locationCallback: LocationCallback? = null // Callback for location updates
    private var lastLocation: Location? = null // Last known location
    private var distance = START_DISTANCE // Total distance traveled

    private var isDebug = true // Flag for enabling debug mode (for development purposes)

    override fun onBind(intent: Intent?): IBinder? =
        null // Not binding this service to any activity

    /**
     * Called when the service is created. Initializes the location settings.
     */
    override fun onCreate() {
        super.onCreate()
        initLocation() // Initialize location settings
    }

    /**
     * Called when the service is started. Sets up the foreground notification
     * and starts location updates.
     *
     * @param intent The intent that started the service.
     * @param flags Additional data about the start request.
     * @param startId A unique integer representing this specific request to start.
     * @return An integer indicating how to continue the service.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            notificationManager.buildNotification() // Build and show the foreground notification
        )
        startLocationUpdates() // Start receiving location updates
        isRunning = true // Set service running flag
        startTime = System.currentTimeMillis() // Record the start time
        return START_STICKY // Indicate that the service should be restarted if terminated
    }

    /**
     * Called when the service is destroyed. Stops location updates and resets state.
     */
    override fun onDestroy() {
        super.onDestroy()
        isRunning = false // Reset service running flag
        startTime = START_TIME // Reset start time
        locationCallback?.let { fusedLocationProvider?.removeLocationUpdates(it) } // Stop location updates
    }

    /**
     * Initializes the location settings, including the location request parameters.
     */
    private fun initLocation() {
        val locationUpdateInterval = PreferenceManager.getDefaultSharedPreferences(this).getString(
            TIME_PREFERENCE_KEY, DEFAULT_UPDATE_TIME
        )?.toLongOrNull() ?: DEFAULT_UPDATE_TIME_LONG // Get update interval from preferences
        fusedLocationProvider =
            LocationServices.getFusedLocationProviderClient(this) // Initialize fused location provider
        locationRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, locationUpdateInterval)
            .setMinUpdateIntervalMillis(locationUpdateInterval) // Set minimum update interval
            .build()
        locationCallback = object : LocationCallback() {

            @SuppressLint("DefaultLocale")
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                if (lastLocation != null) {
                    result.lastLocation?.let { location ->
                        if (location.speed > MIN_SPEED_TO_DETECT_MOVING || isDebug) { // Only process if moving or in debug mode
                            lastLocation?.run {
                                distance += distanceTo(location) // Calculate distance traveled
                            }
                            geoPoints.add(
                                GeoPoint(
                                    location.latitude,
                                    location.longitude
                                )
                            ) // Add new GeoPoint
                        }
                        val distanceFormatted =
                            String.format(FORMAT, distance) // Format distance
                        val speedFormatted =
                            String.format(FORMAT, COEF_TO_KM_H * location.speed) // Format speed
                        val averageSpeedFormatted = String.format(
                            FORMAT,
                            COEF_TO_KM_H * (distance / ((System.currentTimeMillis() - startTime) / MLS_IN_SEC)) // Format average speed
                        )
                        val locationModel = LocationModel(
                            speedFormatted,
                            averageSpeedFormatted,
                            distanceFormatted,
                            geoPoints
                        )
                        locationController.locationData.onNext(locationModel) // Send location data
                    }
                }
                lastLocation = result.lastLocation // Update last known location
            }
        }
    }

    /**
     * Starts location updates if the necessary permissions are granted.
     */
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return // Return if permission is not granted
        if (locationRequest != null && locationCallback != null) {
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest!!,
                locationCallback!!,
                Looper.getMainLooper() // Use the main thread for location updates
            )
        }
    }

    companion object {
        var isRunning = false // Indicates if the service is currently running
        var startTime = START_TIME // Records the start time of the service
    }
}
