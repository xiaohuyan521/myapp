package com.xh.mian.myapp.appitem.home.a
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xh.mian.myapp.tools.uitl.SDCardHelper
import java.io.File

class FinderModel : ViewModel() {

    var butTest = "选择文件"
    var title: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = "文件选择器"
    }
    fun setTitle(s:String){
        title.value=s
    }

    var tips:String = "使用LFilePicker框架\n关联包'com.leon:lfilepickerlibrary:1.8.0'"
    var code:String = "可选择进入路径,默认根目录"

    private var mList: MutableLiveData<List<String>> = MutableLiveData<List<String>>()
    fun setmList() {
        var list:List<String> = mutableListOf()
        val sdcardPath = SDCardHelper.getSDCardBaseDir()
        var file: File = File(sdcardPath)
        if (file.isDirectory) {
            val f = file.listFiles() //获取该文件夹下的文件集合
            for (i in f.indices) {
                if (f[i].isDirectory) {
                    val fileName = f[i].name
                    list += fileName
                }
            }
        }
        mList.setValue(list);
    }
    fun getmList(): LiveData<List<String>> {
        return mList
    }
}
