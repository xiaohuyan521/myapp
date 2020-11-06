package com.sbingo.wanandroid_mvvm.utils

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.xh.mian.myapp.MyApplication
import com.xh.mian.myapp.R


class ToastUtils {

    companion object {
        private var toast: Toast? = null

        fun show(content: String) {
            val inflater = LayoutInflater.from(MyApplication.instance)
            val view = inflater.inflate(R.layout.toast_layout, null)
            val text = view.findViewById(R.id.toast_text) as TextView
            text.text = content
            toast = Toast(MyApplication.instance)
            toast!!.setGravity(Gravity.CENTER, 0, 0)
            toast!!.duration = Toast.LENGTH_SHORT
            toast!!.view = view
            toast!!.show()
        }
    }
}
