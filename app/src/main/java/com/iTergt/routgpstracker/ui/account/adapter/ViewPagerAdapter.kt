package com.iTergt.routgpstracker.ui.account.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment, private val list: List<Fragment>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}