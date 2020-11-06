package com.xh.mian.myapp.appitem.common

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.xh.mian.myapp.R
import com.xh.mian.myapp.databinding.TextViewBinding


class TextActivity : AppCompatActivity() {
    private lateinit var mBinding: TextViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =  DataBindingUtil.setContentView(this, R.layout.text_view)
        val itn = this.intent
        var title = itn.getStringExtra("title")
        var describe = itn.getStringExtra("describe")
        describe = describe?.replace("\\n", "\n")
        mBinding.title.text = title
        mBinding.tips.text = describe
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return false
    }
}