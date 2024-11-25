package com.iTergt.routgpstracker.ui.routeDetails

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.FragmentRouteDetailsBinding
import com.iTergt.routgpstracker.repository.SharedPreferencesRepository
import com.iTergt.routgpstracker.ui.tabs.home.DEFAULT_LINE_WIDTH
import com.iTergt.routgpstracker.ui.tabs.home.DEFAULT_ZOOM
import com.iTergt.routgpstracker.ui.tabs.home.OSM_MAP_PREFERENCES
import com.iTergt.routgpstracker.ui.tabs.routes.ROUTE_DETAILS_ARGS_KEY
import com.iTergt.routgpstracker.ui.tabs.settings.COLOR_PREFERENCE_KEY
import com.iTergt.routgpstracker.ui.tabs.settings.DEFAULT_ROUTE_COLOR
import com.iTergt.routgpstracker.utils.geoPointsConvertFromString
import com.iTergt.routgpstracker.utils.snackbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

/**
 * Fragment responsible for displaying the details of a specific route.
 *
 * This fragment interacts with the [RouteDetailsViewModel] to load and display
 * route information, including the route's date, time, average speed, distance,
 * and geographical points on a map.
 */
class RouteDetailsFragment : Fragment() {

    /**
     * The ViewModel for managing the route details.
     */
    private val viewModel: RouteDetailsViewModel by viewModel()

    /**
     * The SharedPreferencesRepository for getting user uid.
     */
    private val sharedPreference: SharedPreferencesRepository by inject()

    /**
     * Navigation arguments containing the ID of the route to load.
     */
    private var id: Long? = null

    /**
     * Binding object for accessing views in the fragment layout.
     */
    private var binding: FragmentRouteDetailsBinding? = null

    /**
     * Inflates the fragment's layout and initializes the map settings.
     *
     * @param inflater LayoutInflater used to inflate the fragment's layout.
     * @param container Optional parameter for the parent view group.
     * @param savedInstanceState Optional bundle containing saved instance state.
     * @return The root view of the fragment's layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsOsmMap() // Initialize OpenStreetMap settings
        binding = FragmentRouteDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    /**
     * Called after the view has been created. This is where the ViewModel is
     * observed for changes, and UI elements are initialized.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState Optional bundle containing saved instance state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load the route using the provided route ID from arguments if not null
        id = arguments?.getLong(ROUTE_DETAILS_ARGS_KEY)
        id?.let { id ->
            sharedPreference.getUserUid()?.let { uid ->
                viewModel.loadRoute(id, uid)
            }
        }

        // Set up error handling
        viewModel.onError = { message ->
            message?.let {
                binding?.routeDetailsContainer?.snackbar(it) // Show error message in a snackbar
            }
        }

        // Observe route LiveData for updates
        viewModel.route.observe(viewLifecycleOwner) { route ->
            binding?.run {
                // Update UI elements with route details
                tvDetailsDate.text = resources.getString(R.string.route_date, route.date.toString())
                tvDetailsTime.text = resources.getString(R.string.tv_timer, route.time)
                tvDetailsAverageSpeed.text =
                    resources.getString(R.string.tv_averageSpeed, route.averageSpeed)
                tvDetailsDistance.text = resources.getString(R.string.tv_distance, route.distance)

                // Set up the polyline on the map
                val polyline = Polyline()
                polyline.outlinePaint?.color = Color.parseColor(
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
                        .getString(COLOR_PREFERENCE_KEY, DEFAULT_ROUTE_COLOR)
                )
                polyline.outlinePaint?.strokeWidth = DEFAULT_LINE_WIDTH
                detailsMap.overlays.add(polyline)
                polyline.setPoints(geoPointsConvertFromString(route.geoPoints))

                // Set the starting point and markers on the map
                viewModel.setStartPoint(polyline.actualPoints[0])
                setMarkers(polyline.actualPoints)
                goToStartPosition(polyline.actualPoints[0])
            }
        }

        // Set click listener for the button to show the route
        binding?.btnShowRoute?.setOnClickListener {
            showRoute()
        }
    }

    /**
     * Animates the map to the starting point of the route when the button is clicked.
     */
    private fun showRoute() {
        viewModel.startPoint.value?.let { startPoint ->
            binding?.detailsMap?.controller?.animateTo(startPoint)
        }
    }

    /**
     * Centers the map on the starting position of the route.
     *
     * @param startGeoPoint The starting geographical point to center the map on.
     */
    private fun goToStartPosition(startGeoPoint: GeoPoint) {
        binding?.detailsMap?.controller?.run {
            zoomTo(DEFAULT_ZOOM) // Set the zoom level
            animateTo(startGeoPoint) // Animate to the starting point
        }
    }

    /**
     * Sets markers on the map for the start and finish points of the route.
     *
     * @param geoPoints The list of geographical points representing the route.
     */
    private fun setMarkers(geoPoints: List<GeoPoint>) {
        if (geoPoints.isNotEmpty()) {
            binding?.run {
                // Create and configure the start marker
                val startMarker = Marker(detailsMap).apply {
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    position = geoPoints[0] // Set position to the starting point
                    icon = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_start,
                        null
                    ) // Set start icon
                }

                // Create and configure the finish marker
                val finishMarker = Marker(detailsMap).apply {
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    position = geoPoints[geoPoints.size - 1] // Set position to the finishing point
                    icon = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_finish,
                        null
                    ) // Set finish icon
                }

                // Add markers to the map
                detailsMap.overlays.add(startMarker)
                detailsMap.overlays.add(finishMarker)
            }
        }
    }

    /**
     * Initializes the OpenStreetMap settings for the map view.
     */
    private fun settingsOsmMap() {
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences(OSM_MAP_PREFERENCES, Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }
}