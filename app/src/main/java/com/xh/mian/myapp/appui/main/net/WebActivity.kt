package com.xh.mian.myapp.appui.main.net

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.xh.mian.myapp.R
import com.xh.mian.myapp.databinding.TextViewBinding
import com.xh.mian.myapp.databinding.WebViewBinding

class WebActivity  : AppCompatActivity() {
    private lateinit var mBinding: WebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =  DataBindingUtil.setContentView(this, R.layout.web_view)
        var str = this.intent.getStringExtra("title")
        mBinding.title.text = str

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

        mBinding.webview.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
            ): Boolean {
                Log.d("", "On Js Alert %s".format(message))
                return super.onJsAlert(view, url, message, result)
            }
        }

        mBinding.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
            ): Boolean {
                //Log.d("","Override url loading : %s".format(request?.url))
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
        mBinding.webview.loadUrl(url!!)
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return false
    }
}