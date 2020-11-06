package com.xh.mian.myapp.appui.main.home

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.xh.mian.myapp.R
import com.xh.mian.myapp.appui.main.MainActivity
import com.xh.mian.myapp.databinding.FragmentHomeBinding
import com.xh.mian.myapp.tools.dialog.LoadingDialog

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var loadingDialog: LoadingDialog
    // 是否可以加载
    private val toggleLoadMore = true
    private val adapter by lazy { HomeAdapter()}

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        mBinding =  DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        mBinding.apply { homeViewModel }
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            mBinding.title.text = it
        })
        homeViewModel.getmList()?.observe(viewLifecycleOwner, Observer {
            var mapList= it as List<HomeBean>
            adapter.updata(mapList)
            loadingDialog?.stopDialog()
        })
        initData()
        loadingDialog = LoadingDialog(MainActivity.mContex, "正在加载数据...")
        loadingDialog!!.showDialog()
        homeViewModel.refresh_mList()
        return mBinding.root
    }
    private fun initData() {
        initSwipe()
        initRecyclerView()
    }

    private fun initSwipe() {
        //下拉刷新
        mBinding.swipeRefreshLayout.setColorSchemeResources(R.color.blue)
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            loadingDialog = LoadingDialog(MainActivity.mContex, "正在加载数据...")
            loadingDialog!!.showDialog()
            homeViewModel.refresh_mList()
            mBinding.swipeRefreshLayout.isRefreshing = false
        }
        //上拉加载更多
        mBinding.recyclerview.setItemAnimator(DefaultItemAnimator())
        mBinding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            // 用来标记是否正在向上滑动
            private var isSlidingUpward = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val manager = recyclerView.layoutManager as LinearLayoutManager?
                // 当不滑动时
                if (newState === RecyclerView.SCROLL_STATE_IDLE) {
                    // 获取最后一个完全显示的 itemPosition
                    val lastItemPosition = manager!!.findLastCompletelyVisibleItemPosition()
                    val itemCount = manager!!.itemCount
                    // 判断是否滑动到了最后一个item，并且是向上滑动
                    if (lastItemPosition == itemCount - 1 && isSlidingUpward) {
                        if (toggleLoadMore) {
                            adapter.setLoadState(adapter.LOADING);
                            Handler().postDelayed({
                                adapter.setLoadState(adapter.LOADING_END);
                            }, 1000)
                        } else {
                            // 显示加载到底的提示
                            adapter.setLoadState(adapter.LOADING_END);
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView!!, dx, dy)
                isSlidingUpward = dy > 0
            }
        })

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