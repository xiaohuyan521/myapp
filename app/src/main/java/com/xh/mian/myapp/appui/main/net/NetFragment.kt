package com.xh.mian.myapp.appui.main.net

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xh.mian.myapp.R
import com.xh.mian.myapp.appui.main.MainActivity
import com.xh.mian.myapp.databinding.FragmentNetBinding
import com.xh.mian.myapp.tools.dialog.LoadingDialog
import com.xh.mian.myapp.tools.https.NewsBean
import com.xh.mian.myapp.tools.https.RetrofitService
import com.xh.mian.myapp.tools.https.RetrofitUtil
import com.xh.mian.myapp.tools.other.Tools
import com.xh.mian.myapp.tools.view.MyListView.OnViewListener
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class NetFragment : Fragment() {
    private lateinit var netViewModel: NetViewModel
    private lateinit var mBinding: FragmentNetBinding
    private val adapter by lazy { NetAdapter() }
    private var mlist: List<NetBean> = mutableListOf()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        netViewModel = ViewModelProvider(this).get(NetViewModel::class.java)
        mBinding =  DataBindingUtil.inflate(inflater, R.layout.fragment_net, container, false)
        mBinding.viewModel = netViewModel

        setList()
        getData()
        return mBinding.root
    }
    private fun setList(){
        mBinding.listfs.adapter = adapter
        mBinding.listfs.setOkListener(object : OnViewListener {
            override fun onRefresh() {
                Handler().postDelayed(Runnable {
                    getData()
                    mBinding.listfs.stopRefresh();
                }, 1000)
            }

            override fun onLoadMore() { //加载更多
                Handler().postDelayed(Runnable { mBinding.listfs.stopLoadMore(); }, 1000)
            }
        })
    }
    private fun getData(){
        val loadingDialog = LoadingDialog(MainActivity.mContex, "正在加载数据...")
        loadingDialog!!.showDialog()
        mlist = mutableListOf()
        for(url in netViewModel.datas){
            val mTestService: RetrofitService<NewsBean> = RetrofitUtil.getInstance().t.create(RetrofitService::class.java) as RetrofitService<NewsBean>
            mTestService.getWithoutRxJava(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    private var mDisposable: Disposable? = null
                    override fun onSubscribe(d: Disposable) {
                        mDisposable = d
                    }

                    override fun onNext(value: String) {
                        loadingDialog?.stopDialog()
                        @SuppressLint("HandlerLeak") val handler: Handler = object : Handler() {
                            override fun handleMessage(msg: Message) {
                                var data = Tools.dataFilter(value)
                                var bean = NetBean("", "", "", "", "", "","")
                                bean.url = url
                                bean.chapter = data.get("chapter")
                                bean.author = data.get("author")
                                bean.title = data.get("title")
                                bean.desc = data.get("desc")
                                bean.thumbnail = data.get("thumbnail")
                                bean.date = data.get("date")
                                mlist += bean
                                adapter.updata(mlist)
                            }
                        }
                        Thread { handler.sendEmptyMessage(0) }.start()

                        //Toast.makeText(MainActivity.mContex, data.toString(), Toast.LENGTH_SHORT).show()
                        if (mDisposable != null && !mDisposable!!.isDisposed) mDisposable!!.dispose() //注销
                    }

                    override fun onError(e: Throwable) {
                        loadingDialog?.stopDialog()
                        if (mDisposable != null && !mDisposable!!.isDisposed) mDisposable!!.dispose() //注销
                    }

                    override fun onComplete() {
                        loadingDialog?.stopDialog()
                        if (mDisposable != null && !mDisposable!!.isDisposed) mDisposable!!.dispose()
                    }
                })
        }
    }
    //private fun


}