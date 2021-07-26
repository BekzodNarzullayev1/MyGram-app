package com.example.mygram.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(private val fragmentManager:FragmentManager, private val fragments: ArrayList<Fragment>,private val titles:ArrayList<String>):FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int  = fragments.size

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

//    public fun addFragment(fragment: Fragment,title:String) {
//        fragments.add(fragment)
//        titles.add(title)
//    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}