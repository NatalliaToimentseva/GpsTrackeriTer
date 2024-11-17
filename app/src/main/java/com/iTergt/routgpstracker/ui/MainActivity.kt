package com.iTergt.routgpstracker.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.ActivityMainBinding
import com.iTergt.routgpstracker.repository.SharedPreferencesRepository
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val sharedPreference: SharedPreferencesRepository by inject()
    private var binding: ActivityMainBinding? = null
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        val x = sharedPreference.isLoggedIn()
        Log.d("AAA", "x = $x")
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController.apply {
            val navGraph: NavGraph = navInflater.inflate(R.navigation.main_navigation_graph)
            navGraph.setStartDestination(
                if (sharedPreference.isLoggedIn()) {
                    R.id.tabsFragment
                } else {
                    R.id.accountFragment
                }
            )
            graph = navGraph
        }
    }
}