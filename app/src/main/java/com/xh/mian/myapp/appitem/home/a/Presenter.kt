package com.xh.mian.myapp.appitem.home.a

import android.Manifest
import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.leon.lfilepickerlibrary.LFilePicker
import com.leon.lfilepickerlibrary.utils.Constant
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xh.mian.myapp.R
import com.xh.mian.myapp.tools.uitl.SDCardHelper
import java.io.File

class Presenter {
    @SuppressLint("CheckResult")
    fun onClick(view: View) {
        when (view.id) {
            R.id.but_1 -> {
                val rxPermissions = FinderActivity.mContex?.let { RxPermissions(it) }
                rxPermissions?.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ?.subscribe { aBoolean ->
                        if (aBoolean) {
                            //Log.d("##", "accept: 获得了权限")
                            //默认打开文件路径
                            val sdcardPath = SDCardHelper.getSDCardBaseDir() + FinderActivity.mContex!!.patch
                            val destDir = File(sdcardPath)
                            if (!destDir.exists()) {
                                destDir.mkdirs()
                            }
                            LFilePicker()
                                .withActivity(FinderActivity.mContex)
                                .withRequestCode(FinderActivity.mContex!!.REQUESTCODE_FROM)
                                .withIconStyle(Constant.ICON_STYLE_BLUE) //.withFileFilter(new String[]{".mp3", ".wav"})
                                .withMaxNum(1)
                                .withTitle("请选择文件") //标题文字
                                .withMutilyMode(true) //true 多选
                                .withStartPath(sdcardPath)
                                .withIsGreater(false) //过滤文件大小 小于指定大小的文件
                                .withFileSize(150 * 1024 * 1024.toLong()) //指定文件大小为150M
                                .start()
                        } else {
                            //有一个权限未获得就会执行此方法
                            //Log.d("##", "accept: 未获得全部权限")
                            Toast.makeText(FinderActivity.mContex, "获取权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
            }
            else -> {
            }
        }
    }
}