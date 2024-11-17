package com.iTergt.routgpstracker.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.FragmentTabsBinding

class TabsFragment : Fragment() {

    private var binding: FragmentTabsBinding? = null
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTabsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.tab_container) as NavHostFragment
        navController = navHostFragment.navController
        navController?.let {
            binding?.navBar?.setupWithNavController(it)
        }
    }
}