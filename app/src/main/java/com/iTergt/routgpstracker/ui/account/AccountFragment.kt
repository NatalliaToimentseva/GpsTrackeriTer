package com.iTergt.routgpstracker.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.FragmentAccountBinding
import com.iTergt.routgpstracker.ui.account.adapter.ViewPagerAdapter
import com.iTergt.routgpstracker.ui.account.login.LogInFragment
import com.iTergt.routgpstracker.ui.account.signup.SignUpFragment

class AccountFragment : Fragment() {

    private var binding: FragmentAccountBinding? = null
    private val fragmentsList = listOf(
        LogInFragment(),
        SignUpFragment()
    )
    private val tabTitles = listOf(
        R.string.login,
        R.string.signup
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ViewPagerAdapter(this, fragmentsList)
        binding?.run {
            viewPager.adapter = adapter
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = requireContext().getString(tabTitles[position])
            }.attach()
        }
    }
}