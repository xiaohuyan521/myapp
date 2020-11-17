package com.xh.mian.myapp.appitem.project.b

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.xh.mian.myapp.R
import com.xh.mian.myapp.databinding.BinaryFragmentViewBinding

class BinaryFragment2 : Fragment() {
    private lateinit var mBinding: BinaryFragmentViewBinding
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        mBinding =  DataBindingUtil.inflate(inflater, R.layout.binary_fragment_view, container, false)
        return mBinding.root
    }
}