package com.iTergt.routgpstracker.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.FragmentHomeBinding
import com.iTergt.routgpstracker.service.LocationService
import com.iTergt.routgpstracker.utils.snackbar
import com.iTergt.routgpstracker.utils.snackbarWithListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

private const val OSM_MAP_PREFERENCES = "osm_pref"
private const val SCHEME = "package"
private const val FORMAT = "%.1f"
private const val MLS_IN_SEC = 1000.0f
private const val COEF_TO_KM_H = 3.6f

class HomeFragment : Fragment() {

    private val permissions = arrayListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val viewModel: HomeViewModel by viewModel()
    private var pLauncher: ActivityResultLauncher<Array<String>>? = null
    private var binding: FragmentHomeBinding? = null
    private var polyline: Polyline? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsOsmMap() // it is necessary for this library to initialize it before xml
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
        if (!isInternetConnection()) {
            binding?.map?.snackbar(resources.getString(R.string.check_internet_message))
        }
        checkServiceState()
        viewModel.timePassed.observe(viewLifecycleOwner) { time ->
            binding?.tvTime?.text = resources.getString(R.string.tv_timer, time)
        }
        viewModel.locationData.observe(viewLifecycleOwner) { location ->
            val distanceFormated = String.format(FORMAT, location.distance)
            val speedFormated = String.format(FORMAT, COEF_TO_KM_H * location.speed)
            val averageSpeedFormated = String.format(
                FORMAT,
                COEF_TO_KM_H * (location.distance / ((System.currentTimeMillis() - LocationService.startTime) / MLS_IN_SEC))
            )
            binding?.run {
                tvDistance.text = resources.getString(R.string.tv_distance, distanceFormated)
                tvSpeed.text = resources.getString(R.string.tv_speed, speedFormated)
                tvAverageSpeed.text =
                    resources.getString(R.string.tv_averageSpeed, averageSpeedFormated)
            }
            if (viewModel.isMapInitialized.value == true) {
                updatePolyline(location.geoPointsList)
            }
        }
        binding?.btnStartStop?.setOnClickListener {
            if (viewModel.isServiceRunning.value == false) {
                startLocationService()
            } else {
                stopLocationService()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isPermissionGranted.value == true) {
            if (viewModel.isMapInitialized.value == false) {
                initOsmMap()
            }
        } else {
            showPermissionDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        polyline = null
        viewModel.setIsMapInitialized(false)
        viewModel.setIsFirstStart(true)
    }

    private fun settingsOsmMap() {
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences(OSM_MAP_PREFERENCES, Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun initOsmMap() {
        binding?.run {
            polyline = Polyline()
            polyline?.outlinePaint?.color = Color.GREEN
            map.controller.setZoom(20.0)
            val myLocationProvider = GpsMyLocationProvider(requireContext())
            val myLocationOverlay = MyLocationNewOverlay(myLocationProvider, map)
            myLocationOverlay.enableMyLocation()
            myLocationOverlay.enableFollowLocation()
            myLocationOverlay.runOnFirstFix {
                map.overlays.clear()
                map.overlays.add(myLocationOverlay)
                map.overlays.add(polyline)
            }
        }
        viewModel.setIsMapInitialized(true)
    }

    private fun drawPoint(list: List<GeoPoint>) {
        if (list.isNotEmpty()) {
            polyline?.addPoint(list[list.size - 1])
        }
    }

    private fun drawAllPoints(list: List<GeoPoint>) {
            list.forEach { point ->
                polyline?.addPoint(point)
            }
    }

    private fun updatePolyline(list: List<GeoPoint>) {
        if (list.size > 1 && viewModel.isFirstStart.value == true) {
            drawAllPoints(list)
            viewModel.setIsFirstStart(false)
        } else {
            drawPoint(list)
        }
    }

    private fun requestPermission() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            if (permission.getValue(Manifest.permission.ACCESS_FINE_LOCATION) ||
                permission.getValue(Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                viewModel.setPermissionStatus(true)
                initOsmMap()
                if (!isLocationAvailable()) {
                    binding?.map?.snackbarWithListener(
                        resources.getString(R.string.snackbar_gps_message),
                        resources.getString(R.string.snackbar_gps_action),
                    ) {
                        ContextCompat.startActivity(
                            requireContext(),
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                            null
                        )
                    }
                }
            } else {
                showPermissionDialog()
            }
        }.apply {
            launch(permissions.toTypedArray())
        }
    }

    private fun isInternetConnection(): Boolean {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetwork != null
    }

    private fun isLocationAvailable(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showPermissionDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.request_permission_title))
            .setMessage(resources.getString(R.string.request_permission_message))
            .setPositiveButton(getString(R.string.positive_button)) { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts(SCHEME, requireContext().packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ContextCompat.startActivity(requireContext(), intent, null)
            }
            .setNegativeButton(getString(R.string.negative_button)) { _, _ -> }
            .create()
            .show()
    }

    private fun startLocationService() {
        requireContext().startForegroundService(
            Intent(
                requireContext(),
                LocationService::class.java
            )
        )
        viewModel.setIsServiceRunning(true)
        viewModel.startTimer()
        binding?.btnStartStop?.setImageResource(R.drawable.btn_stop)
    }

    private fun stopLocationService() {
        viewModel.setIsServiceRunning(false)
        viewModel.stopTimer()
        binding?.btnStartStop?.setImageResource(R.drawable.btn_start)
        requireContext().stopService(Intent(requireContext(), LocationService::class.java))
        polyline?.setPoints(arrayListOf())
    }

    private fun checkServiceState() {
        if (LocationService.isRunning) {
            viewModel.setIsServiceRunning(true)
            viewModel.startTimer()
            binding?.btnStartStop?.setImageResource(R.drawable.btn_stop)
        }
    }
}