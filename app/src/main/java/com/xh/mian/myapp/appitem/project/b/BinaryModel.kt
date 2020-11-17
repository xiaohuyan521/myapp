package com.xh.mian.myapp.appitem.project.b

import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BinaryModel : ViewModel() {
    var str: MutableLiveData<String> = MutableLiveData<String>()
    fun WarlabTest(warlabtest: TableLayout){
        val ccCnt = warlabtest.childCount
        for (i in 0 until ccCnt) {
            val vgcv: View = warlabtest.getChildAt(i)
            if (vgcv is TableRow) {
                val len = vgcv.childCount
                for (j in 0 until len) {
                    val tv: View = vgcv.getChildAt(j)
                    if (tv is TextView) {
                        tv.setOnClickListener {
                            str.setValue(tv.text.toString())
                        }
                    }
                }
            }
        }
    }
}