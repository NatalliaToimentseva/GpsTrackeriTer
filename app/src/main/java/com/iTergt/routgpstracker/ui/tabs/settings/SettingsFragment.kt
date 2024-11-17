package com.iTergt.routgpstracker.ui.tabs.settings

import android.graphics.Color
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.repository.SharedPreferencesRepository
import com.iTergt.routgpstracker.utils.findTopNavController
import org.koin.android.ext.android.inject

const val COLOR_PREFERENCE_KEY = "route_color_key"
const val TIME_PREFERENCE_KEY = "update_time_key"
const val LOGOUT_PREFERENCE_KEY = "logout_key"
const val DEFAULT_UPDATE_TIME = "3000"
const val DEFAULT_ROUTE_COLOR = "#FF2196F3"
private const val SEPARATOR = ":"

class SettingsFragment : PreferenceFragmentCompat() {

    private val sharedPreference: SharedPreferencesRepository by inject()
    private val firebaseAuth: FirebaseAuth by inject()
    private var timePref: Preference? = null
    private var colorPref: Preference? = null
    private var logoutPreference: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference_screen, rootKey)
        init()
    }

    private fun init() {
        timePref = findPreference(TIME_PREFERENCE_KEY)
        colorPref = findPreference(COLOR_PREFERENCE_KEY)
        logoutPreference = findPreference(LOGOUT_PREFERENCE_KEY)
        timePref?.onPreferenceChangeListener = onChangedListener()
        colorPref?.onPreferenceChangeListener = onChangedListener()
        logoutPreference?.setOnPreferenceClickListener {
            logout()
            true
        }
        initPref()
    }

    private fun onChangedListener(): OnPreferenceChangeListener {
        return OnPreferenceChangeListener { preference, value ->
            when (preference.key) {
                TIME_PREFERENCE_KEY -> onTimeChanged(value.toString())
                COLOR_PREFERENCE_KEY -> preference.icon?.setTint(
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

    private fun logout() {
        firebaseAuth.signOut()
        sharedPreference.setLoggedIn(false)
        sharedPreference.clearUserData()
        findTopNavController().popBackStack()
        findTopNavController().navigate(R.id.accountFragment)
    }
}