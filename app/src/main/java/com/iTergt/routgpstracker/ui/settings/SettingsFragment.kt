package com.iTergt.routgpstracker.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.iTergt.routgpstracker.R

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var timePref: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference_screen, rootKey)
        init()
    }

    private fun init() {
        timePref = findPreference("update_time_key")!!
        timePref.onPreferenceChangeListener = onChangedListener()
    }

    private fun onChangedListener(): OnPreferenceChangeListener {
        return OnPreferenceChangeListener { preference, value ->
            val nameArr = resources.getStringArray(R.array.loc_time_update_name)
            val mlsArr = resources.getStringArray(R.array.loc_time_update_mls)
            val title = preference.title.toString().substringBefore(":")
            preference.title = "$title: ${nameArr[mlsArr.indexOf(value)]}"
            true
        }
    }
}