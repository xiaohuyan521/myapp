package com.xh.mian.myapp.appitem.project.b

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xh.mian.myapp.R
import com.xh.mian.myapp.databinding.BinaryFragmentViewBinding
import java.util.regex.Matcher
import java.util.regex.Pattern


class BinaryFragment1 : Fragment() {
    private lateinit var mBinding: BinaryFragmentViewBinding
    private lateinit var model: BinaryModel
    private var str:String?=null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        mBinding =  DataBindingUtil.inflate(inflater, R.layout.binary_fragment_view, container, false)
        model = ViewModelProvider(this).get(BinaryModel::class.java)
        mBinding.apply { model }
        model.str.observe(viewLifecycleOwner, Observer {
            var op = mBinding.tips.text.toString()
            var value: String = op + it
            mBinding.tips.text = value
            if ("=" == it) {
                var arry = value.toCharArray()
                var str:String=""


                //优先计算 高位运算符  拼接成新的字符串 再计算地位运算符
                /*var
                for (i in arry.indices) {
                    var s:String = arry[i].toString()
                    if(isNumeric(s)){
                        str+=s
                    }else{
                        str = ""
                    }
                }*/

            }
        })
        model.WarlabTest(mBinding.warlabtest)
        return mBinding.root
    }

    var HighOrder:List<String> = arrayListOf("x","÷")


    private fun isNumeric(str: String?): Boolean {
        val pattern: Pattern = Pattern.compile("[0-9]*")
        val isNum: Matcher = pattern.matcher(str)
        return isNum.matches()
    }
}