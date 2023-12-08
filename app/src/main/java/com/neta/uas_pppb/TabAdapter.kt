package com.neta.uas_pppb

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.search.SearchView.Behavior

class TabAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){


    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> ListmovieFragment()
            1 -> AddmovieFragment()
            else -> throw IllegalAccessException("Invalid Tab Positon")
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Your List"
            1 -> "Add Movie"
            else -> null
        }
    }



}