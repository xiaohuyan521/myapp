package com.xh.mian.myapp.appui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xh.mian.myapp.MyApplication
import com.xh.mian.myapp.tools.db.DbHelper


class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "功能"
    }
    val text: LiveData<String> = _text

    var mList: MutableLiveData<List<HomeBean>> = MutableLiveData<List<HomeBean>>()
    fun getmList(): LiveData<List<HomeBean>> {
        return mList
    }
    fun refresh_mList() {
        var ls:List<HomeBean> = mutableListOf()
        var db = DbHelper(MyApplication.instance)
        var list = db.getListByTableName("home", "", null, null)
        for (map in list){
            var bean = HomeBean("", "", "", null, "","")
            try {
                bean.title = map["title"]?.toString()
                bean.item = map["item"]?.toString()
                bean.image = map["image"]?.toString()
                bean.describe = map["describe"]?.toString()
                bean.time = map["time"]?.toString()
                var activityName= map["obj"]?.toString()
                bean.obj = Class.forName(activityName)
            }catch (e:Exception){
                e.message
            }
            ls+=bean
        }
        mList.setValue(ls);
    }

}