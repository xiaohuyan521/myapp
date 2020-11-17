package com.xh.mian.myapp.appitem.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentAdapter: FragmentPagerAdapter{
    var fragments: MutableList<Fragment> = ArrayList()
    constructor(fm: FragmentManager,fragment: MutableList<Fragment>) : super(fm) {
       this.fragments = fragment
    }
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
    override fun getCount(): Int = fragments.size
}