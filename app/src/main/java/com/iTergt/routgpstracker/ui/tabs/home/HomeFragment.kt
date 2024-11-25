package com.iTergt.routgpstracker.ui.tabs.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.FragmentHomeBinding
import com.iTergt.routgpstracker.models.RouteModel
import com.iTergt.routgpstracker.repository.SharedPreferencesRepository
import com.iTergt.routgpstracker.service.LocationService
import com.iTergt.routgpstracker.ui.tabs.settings.COLOR_PREFERENCE_KEY
import com.iTergt.routgpstracker.ui.tabs.settings.DEFAULT_ROUTE_COLOR
import com.iTergt.routgpstracker.utils.geoPointsConvertToString
import com.iTergt.routgpstracker.utils.snackbar
import com.iTergt.routgpstracker.utils.snackbarWithListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.time.LocalDate

const val OSM_MAP_PREFERENCES = "osm_pref"
const val SCHEME = "package"
const val DEFAULT_ZOOM = 20.0
const val DEFAULT_LINE_WIDTH = 15.0f

class HomeFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissions = arrayListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )
    private val viewModel: HomeViewModel by viewModel()
    private val sharedPreference: SharedPreferencesRepository by inject()
    private var pLauncher: ActivityResultLauncher<Array<String>>? = null
    private var binding: FragmentHomeBinding? = null
    private var myLocationOverlay: MyLocationNewOverlay? = null
    private var polyline: Polyline? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsOsmMap() // it is necessary for this library to initialize it before xml
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreference.setLoggedIn(true)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
            binding?.run {
                tvDistance.text = resources.getString(R.string.tv_distance, location.distance)
                tvSpeed.text = resources.getString(R.string.tv_speed, location.speed)
                tvAverageSpeed.text =
                    resources.getString(R.string.tv_averageSpeed, location.averageSpeed)
            }
            if (viewModel.isMapInitialized.value == true) {
                updatePolyline(location.geoPointsList)
            }
        }
        viewModel.operationResult = { message ->
            if (message != null) {
                binding?.homeContainer?.snackbar(message)
            } else {
                binding?.homeContainer?.snackbar(resources.getString(R.string.save_route_success))
            }
        }
        viewModel.isShowDialog.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                showSaveRouteDialog()
            }

        }
        binding?.btnStartStop?.setOnClickListener {
            if (viewModel.isServiceRunning.value == false) {
                startLocationService()
            } else {
                stopLocationService()
            }
        }
        binding?.btnCurrentLocation?.setOnClickListener {
            showCurrentLocation()
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
            polyline?.outlinePaint?.color = Color.parseColor(
                PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(
                    COLOR_PREFERENCE_KEY, DEFAULT_ROUTE_COLOR
                )
            )
            polyline?.outlinePaint?.strokeWidth = DEFAULT_LINE_WIDTH
            map.controller.setZoom(DEFAULT_ZOOM)
            val myLocationProvider = GpsMyLocationProvider(requireContext())
            myLocationOverlay = MyLocationNewOverlay(myLocationProvider, map).apply {
                enableMyLocation()
                enableFollowLocation()
                runOnFirstFix {
                    map.overlays.clear()
                    map.overlays.add(polyline)
                    map.overlays.add(myLocationOverlay)
                }
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

    private fun showCurrentLocation() {
        myLocationOverlay?.myLocation?.let { location ->
            binding?.map?.controller?.animateTo(location)
        }
        myLocationOverlay?.enableFollowLocation()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                        requireContext().startActivity(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
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
                requireContext().startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts(SCHEME, requireContext().packageName, null)
                    )
                )
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
        viewModel.setShowDialog(true)
        polyline?.setPoints(arrayListOf())
    }

    private fun showSaveRouteDialog() {
        val averageSped =
            viewModel.locationData.value?.averageSpeed
                ?: resources.getString(R.string.start_averageSpeed)
        val distance =
            viewModel.locationData.value?.distance ?: resources.getString(R.string.start_distance)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.save_route_dialog_title))
            .setMessage(
                resources.getString(
                    R.string.save_route_dialog_message,
                    viewModel.timePassed.value,
                    averageSped,
                    distance
                )
            )
            .setPositiveButton(getString(R.string.positive_button)) { _, _ ->
                sharedPreference.getUserUid()?.let { uid ->
                    viewModel.locationData.value?.run {
                        viewModel.saveRoute(
                            RouteModel(
                                0,
                                LocalDate.now(),
                                viewModel.timePassed.value ?: START_TIME,
                                averageSpeed,
                                distance,
                                geoPointsConvertToString(geoPointsList),
                                uid
                            )
                        )
                    }
                }
                viewModel.setShowDialog(false)
            }
            .setNegativeButton(getString(R.string.negative_button)) { _, _ ->
                viewModel.setShowDialog(false)
            }
            .setOnDismissListener { viewModel.setShowDialog(false) }
            .create()
            .show()
    }

    private fun checkServiceState() {
        if (LocationService.isRunning) {
            viewModel.setIsServiceRunning(true)
            viewModel.startTimer()
            binding?.btnStartStop?.setImageResource(R.drawable.btn_stop)
        }
    }
}