package com.iTergt.routgpstracker.ui.settings

import android.graphics.Color
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.iTergt.routgpstracker.R

private const val DEFAULT_UPDATE_TIME = "3000"
private const val DEFAULT_ROUTE_COLOR = "#FF2196F3"
private const val SEPARATOR = ":"

class SettingsFragment : PreferenceFragmentCompat() {

    private var timePref: Preference? = null
    private var colorPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference_screen, rootKey)
        init()
    }

    private fun init() {
        timePref = findPreference(resources.getString(R.string.update_time_key))
        colorPref = findPreference(resources.getString(R.string.route_color_key))
        timePref?.onPreferenceChangeListener = onChangedListener()
        colorPref?.onPreferenceChangeListener = onChangedListener()
        initPref()
    }

    private fun onChangedListener(): OnPreferenceChangeListener {
        return OnPreferenceChangeListener { preference, value ->
            when (preference.key) {
                resources.getString(R.string.update_time_key) -> onTimeChanged(value.toString())
                resources.getString(R.string.route_color_key) -> preference.icon?.setTint(
                    Color.parseColor(
                        value.toString()
                    )
                )
            }
            true
        }
    }

    private fun onTimeChanged(value: String) {
        val nameArr = resources.getStringArray(R.array.loc_time_update_name)
        val mlsArr = resources.getStringArray(R.array.loc_time_update_mls)
        val title = timePref?.title.toString().substringBefore(SEPARATOR)
        timePref?.title = "$title: ${nameArr[mlsArr.indexOf(value)]}"
    }

    private fun initPref() {
        val nameArr = resources.getStringArray(R.array.loc_time_update_name)
        val mlsArr = resources.getStringArray(R.array.loc_time_update_mls)
        val pref = timePref?.preferenceManager?.sharedPreferences
        timePref?.title = "${timePref?.title}: ${
            nameArr[mlsArr.indexOf(
                pref?.getString(
                    resources.getString(R.string.update_time_key),
                    DEFAULT_UPDATE_TIME
                )
            )]
        }"
        val routeColor =
            pref?.getString(resources.getString(R.string.route_color_key), DEFAULT_ROUTE_COLOR)
        colorPref?.icon?.setTint(Color.parseColor(routeColor))
    }
}