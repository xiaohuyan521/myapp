package com.xh.mian.myapp.appui.main.net

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xh.mian.myapp.MyApplication
import com.xh.mian.myapp.appui.main.home.HomeAdapter
import com.xh.mian.myapp.appui.main.home.HomeBean
import com.xh.mian.myapp.tools.db.DbHelper
import java.lang.Exception

class NetViewModel : ViewModel() {
    val title = "网络"
    private val _text = MutableLiveData<String>().apply {
        value = "网络"
    }
    val text: LiveData<String> = _text

    //lateinit 只用于变量 var，而 lazy 只用于常量 val
    //lazy 应用于单例模式(if-null-then-init-else-return)，而且当且仅当变量被第一次调用的时候，委托方法才会执行。
    val datas by lazy { refresh_mList() }
    //var datas = refresh_mList()
    fun refresh_mList() : List<String>{
        var ls:List<String> = mutableListOf()
        var db = DbHelper(MyApplication.instance)
        var list = db.getListByTableName("net", "", null, null)
        for (map in list) {
            try {
                ls += map["url"].toString()
            }catch (e:Exception){
                e.message
            }
        }
        return ls
    }
}