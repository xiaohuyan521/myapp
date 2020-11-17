package com.xh.mian.myapp.appui.main.net

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.xh.mian.myapp.R
import com.xh.mian.myapp.appui.main.MainActivity
import com.xh.mian.myapp.databinding.WebViewBinding
import com.xh.mian.myapp.tools.dialog.LoadingDialog


class WebActivity  : AppCompatActivity() {
    private lateinit var mBinding: WebViewBinding
    private var fail:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =  DataBindingUtil.setContentView(this, R.layout.web_view)
        var str = this.intent.getStringExtra("title")
        mBinding.title.text = str
        initWebView()
    }

    private fun initWebView(){

        var url = this.intent.getStringExtra("url")
        mBinding.webview.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            useWideViewPort = true
            domStorageEnabled = true
        }
        mBinding.webview.settings.setSupportZoom(true)
        mBinding.webview.settings.setBuiltInZoomControls(true);
        mBinding.webview.settings.setDisplayZoomControls(false)

        mBinding.webview.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                mBinding.lvLoading.visibility = View.GONE
                if(!fail){
                    mBinding.webview.visibility = View.VISIBLE
                    mBinding.lvError.visibility = View.GONE
                }
            }
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                mBinding.lvLoading.visibility = View.GONE
                mBinding.webview.visibility = View.GONE
                mBinding.lvError.visibility = View.VISIBLE
                fail = true
            }
            //旧版本，会在新版本中也可能被调用
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {}
        })

        mBinding.webview.visibility = View.INVISIBLE
        mBinding.lvError.visibility = View.GONE
        mBinding.lvLoading.visibility = View.VISIBLE
        mBinding.webview.loadUrl(url!!)
        fail = false
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return false
    }
    fun setupdata(v: View){
        initWebView()
    }
}