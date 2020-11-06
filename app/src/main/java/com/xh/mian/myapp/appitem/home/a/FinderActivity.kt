package com.xh.mian.myapp.appitem.home.a

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.leon.lfilepickerlibrary.utils.Constant
import com.xh.mian.myapp.R
import com.xh.mian.myapp.databinding.FinderViewBinding
import com.xh.mian.myapp.tools.uitl.SDCardHelper
import java.io.File


class FinderActivity : AppCompatActivity() {
    companion object {
        var mContex: FinderActivity? = null
    }
    val REQUESTCODE_FROM = 100
    var patch:String=""
    private lateinit var findermodel: FinderModel
    private lateinit var mBinding: FinderViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Looper.myQueue().addIdleHandler { // 在这里去处理你想延时加载的东西
            // 最后返回false，后续不用再监听了。
            initModel()
            getModel()
            setModel()
            false
        }
    }
    private fun initModel(){
        mContex = this;
        mBinding =  DataBindingUtil.setContentView(this, R.layout.finder_view);
        findermodel = ViewModelProvider(this).get(FinderModel::class.java)

        mBinding.findermodel = findermodel//关联 布局中data ->variable
        mBinding.presenter = Presenter()//关联点击事件
    }
    private fun getModel(){
        //ViewModel  响应式编程
        findermodel.title.observe(this, Observer {
            mBinding.title.text = it
        })
        findermodel.getmList()?.observe(this, Observer {
            var mList: List<String> = it
            // 打印List集合里面的数据
            for (lis in mList) {
                var check: RadioButton = RadioButton(this)
                check.text = lis
                check.setOnClickListener {
                    if (check.isChecked) {
                        patch = "/${check.text.toString()}"
                    }
                }
                mBinding.radioBox.addView(check)
            }
        })
    }

    private fun setModel(){
        Handler().postDelayed({
            findermodel.setTitle("文件选择器-T")
        }, 1000)

        findermodel.setmList()

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE_FROM) {
            try {
                //如果是文件选择模式，需要获取选择的所有文件的路径集合
                //val list: List<String>? = data?.getStringArrayListExtra(Constant.RESULT_INFO) //Constant.RESULT_INFO == "paths"
                //Toast.makeText(applicationContext, "选中了" + list!!.size + "个文件", Toast.LENGTH_SHORT).show()
                //如果是文件夹选择模式，需要获取选择的文件夹路径
                var path:String = data?.getStringExtra("path").toString()
                Toast.makeText(getApplicationContext(), "选中的路径为" + path, Toast.LENGTH_SHORT).show();
            } catch (e: Exception) {
                e.message
            }
        }
    }

}

