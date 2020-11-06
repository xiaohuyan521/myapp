package com.xh.mian.myapp.appui.main.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.xh.mian.myapp.R
import com.xh.mian.myapp.databinding.FragmentProBinding
import com.xh.mian.myapp.tools.adapter.LoadMoreWrapper

class proFragment : Fragment() {
    private lateinit var proViewModel: ProViewModel
    private lateinit var mBinding: FragmentProBinding

    private val adapter by lazy {
        LoadMoreWrapper(ProAdapter(mutableListOf()))
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        proViewModel = ViewModelProvider(this).get(ProViewModel::class.java)
        mBinding =  DataBindingUtil.inflate(inflater, R.layout.fragment_pro, container, false)
        mBinding.proViewModel = proViewModel
        initData()

        proViewModel.getmList().observe(viewLifecycleOwner, Observer {
            mBinding.recyclerview.adapter = LoadMoreWrapper(ProAdapter(it))
        })

        proViewModel.refresh_mList()
        return mBinding.root
    }

    private fun initData() {
        initSwipe()
        initRecyclerView()
    }

    private fun initSwipe() {
        mBinding.swipeRefreshLayout.setColorSchemeResources(R.color.blue)
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mBinding.swipeRefreshLayout.isRefreshing = false
            proViewModel.refresh_mList()
        }
    }

    private fun initRecyclerView() {
        mBinding.recyclerview.adapter = adapter
        mBinding.recyclerview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        mBinding.recyclerview.setLayoutManager(layoutManager)
        layoutManager.orientation = OrientationHelper.VERTICAL!!
        mBinding.recyclerview.setNestedScrollingEnabled(false)
    }
}