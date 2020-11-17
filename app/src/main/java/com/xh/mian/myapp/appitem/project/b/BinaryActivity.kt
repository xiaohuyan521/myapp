package com.xh.mian.myapp.appitem.project.b

import android.os.Bundle
import android.os.Looper
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.xh.mian.myapp.R
import com.xh.mian.myapp.appitem.common.FragmentAdapter
import com.xh.mian.myapp.databinding.BinaryViewBinding


class BinaryActivity: AppCompatActivity() {
    private lateinit var mBinding: BinaryViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =  DataBindingUtil.setContentView(this, R.layout.binary_view);
        Looper.myQueue().addIdleHandler { // 在这里去处理你想延时加载的东西
            TabLayout()
            false
        }
    }
    private fun TabLayout(){
        //标签数据
        val list = listOf("位运算", "计算器", "单位换算")
        var fragments: MutableList<Fragment> = ArrayList()
        fragments.add(BinaryFragment1())
        fragments.add(BinaryFragment2())
        fragments.add(BinaryFragment3())
        mBinding.mtransMainViewpager.adapter = FragmentAdapter(supportFragmentManager, fragments)
        mBinding.mtransMainTablayout.setupWithViewPager(mBinding.mtransMainViewpager)
        //TabLayout设置标题
        for (i in list.indices) {
            val tab = mBinding.mtransMainTablayout?.getTabAt(i)
            tab?.text = list[i]
            //mtransMainTablayout.setBackgroundResource(R.drawable.but_bg_blue);
        }

        //mBinding.mtransMainViewpager.setCurrentItem(default_tab)
        //mBinding.mtransMainTablayout.getTabAt(default_tab).select()

        mBinding.mtransMainTablayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //选中某个tab
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                //当tab从选择到未选择
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                //已经选中tab后的重复点击tab
            }
        })
    }
}