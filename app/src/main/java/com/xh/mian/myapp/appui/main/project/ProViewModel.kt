package com.xh.mian.myapp.appui.main.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xh.mian.myapp.MyApplication
import com.xh.mian.myapp.appitem.home.a.FinderActivity
import com.xh.mian.myapp.appitem.project.a.PdfActivity
import com.xh.mian.myapp.appui.main.home.HomeBean
import com.xh.mian.myapp.tools.db.DbHelper
import com.xh.mian.myapp.tools.uitl.SDCardHelper
import java.io.File

class ProViewModel : ViewModel() {
    var title = "应用"

    //可变集合类（Mutable）
    //不可变集合类（Immutable）。
    //使用listOf函数来构建一个不可变的List(只读的List)
    var mList: MutableLiveData<List<ProBean>> = MutableLiveData<List<ProBean>>()
    fun getmList(): LiveData<List<ProBean>> {
        return mList
    }
    fun refresh_mList() {
        var ls:List<ProBean> = mutableListOf()
        var db = DbHelper(MyApplication.instance)
        var list = db.getListByTableName("pro", "", null, null)
        for (map in list){
            var bean = ProBean("", "", null)
            try {
                bean.title = map["title"]?.toString()
                bean.subtitle = map["subtitle"]?.toString()
                var activityName= map["class"]?.toString()
                bean.obj = Class.forName(activityName)
            }catch (e:Exception){
                e.message
            }
            ls+=bean
        }
        mList.setValue(ls);
    }
}