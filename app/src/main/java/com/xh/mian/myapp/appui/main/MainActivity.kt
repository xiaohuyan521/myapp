package com.xh.mian.myapp.appui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sbingo.wanandroid_mvvm.utils.ToastUtils
import com.xh.mian.myapp.MyApplication
import com.xh.mian.myapp.R
import com.xh.mian.myapp.tools.uitl.PermissionsActivity
import com.xh.mian.myapp.tools.uitl.PermissionsChecker
import io.reactivex.annotations.NonNull


class MainActivity : AppCompatActivity() {
    private var lastExitTime: Long = 0
    companion object {
        var mContex:MainActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContex = this
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

//        mPermissionsChecker = PermissionsChecker(this);
//        //延迟加载 启动优化
//        Looper.myQueue().addIdleHandler { // 在这里去处理你想延时加载的东西
//            // 最后返回false，后续不用再监听了。
//            false
//        }
    }

    override fun onResume() {
        super.onResume()
        //loadPermissions();
    }
    override fun onPause(){
        super.onPause()
    }
    override fun onDestroy(){
        super.onDestroy()
    }
    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
            when (item.getItemId()) {
                R.id.navigation_home -> {
                    return true
                }
                R.id.navigation_pro -> {
                    return true
                }
                R.id.navigation_net -> {
                    return true
                }
                R.id.navigation_person -> {
                    return true
                }
            }
            return false
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastExitTime > 1500) {
                Toast.makeText(MainActivity@this, getString(R.string.exit_hint), Toast.LENGTH_SHORT).show();
                lastExitTime = currentTime
                return true
            } else {
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish()
        }
    }
    /**
     * 动态加载权限
     */
    private var mPermissionsChecker // 权限检测器
            : PermissionsChecker? = null

    // 所需的全部权限
    val PERMISSIONS = arrayOf<String>(
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE
    )
    private val REQUEST_CODE = 0 // 请求码
    private fun loadPermissions() {
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker!!.lacksPermissions(*PERMISSIONS)) {
            PermissionsActivity.startActivityForResult(this, REQUEST_CODE, *PERMISSIONS)
        }
    }
}