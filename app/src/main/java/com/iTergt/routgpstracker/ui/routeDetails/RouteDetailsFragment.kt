package com.iTergt.routgpstracker.ui.routeDetails

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.FragmentRouteDetailsBinding
import com.iTergt.routgpstracker.ui.home.OSM_MAP_PREFERENCES
import com.iTergt.routgpstracker.ui.settings.COLOR_PREFERENCE_KEY
import com.iTergt.routgpstracker.ui.settings.DEFAULT_ROUTE_COLOR
import com.iTergt.routgpstracker.utils.geoPointsConvertFromString
import com.iTergt.routgpstracker.utils.snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class RouteDetailsFragment : Fragment() {

    private val viewModel: RouteDetailsViewModel by viewModel()
    private val arguments: RouteDetailsFragmentArgs by navArgs()
    private var binding: FragmentRouteDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsOsmMap()
        binding = FragmentRouteDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadRoute(arguments.id)
        viewModel.onError = { message ->
            if (message != null) {
                binding?.routeDetailsContainer?.snackbar(message)
            }
        }
        viewModel.route.observe(viewLifecycleOwner) { route ->
            binding?.run {
                tvDetailsDate.text = resources.getString(R.string.route_date, route.date.toString())
                tvDetailsTime.text = resources.getString(R.string.tv_timer, route.time)
                tvDetailsAverageSpeed.text =
                    resources.getString(R.string.tv_averageSpeed, route.averageSpeed)
                tvDetailsDistance.text = resources.getString(R.string.tv_distance, route.distance)
                val polyline = Polyline()
                polyline.outlinePaint?.color = Color.parseColor(
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
                        .getString(COLOR_PREFERENCE_KEY, DEFAULT_ROUTE_COLOR)
                )
                detailsMap.overlays.add(polyline)
                polyline.setPoints(geoPointsConvertFromString(route.geoPoints))
                viewModel.setStartPoint(polyline.actualPoints[0])
                setMarkers(polyline.actualPoints)
                goToStartPosition(polyline.actualPoints[0])
            }
        }
        binding?.btnShowRoute?.setOnClickListener {
            showRoute()
        }
    }

    private fun showRoute() {
        viewModel.startPoint.value?.let { startPoint ->
            binding?.detailsMap?.controller?.animateTo(startPoint)
        }
    }

    private fun goToStartPosition(startGeoPoint: GeoPoint) {
        binding?.detailsMap?.controller?.run {
            zoomTo(18.0)
            animateTo(startGeoPoint)
        }
    }

    private fun setMarkers(geoPoints: List<GeoPoint>) {
        if (geoPoints.isNotEmpty()) {
            binding?.run {
                val startMarker = Marker(detailsMap)
                val finishMarker = Marker(detailsMap)
                startMarker.apply {
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    position = geoPoints[0]

                }
                finishMarker.apply {
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    position = geoPoints[geoPoints.size - 1]
                    icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_finish, null)
                }
                detailsMap.overlays.add(startMarker)
                detailsMap.overlays.add(finishMarker)
            }
        }
    }

    private fun settingsOsmMap() {
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences(OSM_MAP_PREFERENCES, Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }
}