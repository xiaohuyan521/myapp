package com.xh.mian.myapp.appui.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xh.mian.myapp.R
import com.xh.mian.myapp.appui.login.PhoneUtils.SimInfo
import com.xh.mian.myapp.appui.main.MainActivity
import com.xh.mian.myapp.tools.db.SharedPreferences

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        SharedPreferences.save("welcome", true)
        val rxPermissions = RxPermissions(this@LoginActivity)
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE)
            .subscribe { aBoolean ->
                if (aBoolean) {
                    var simInfos = PhoneUtils.getSimMultiInfo()
                    //Log.i("phone=", phone)
                } else {
                    //有一个权限未获得就会执行此方法
                    Toast.makeText(this@LoginActivity, "获取权限失败", Toast.LENGTH_SHORT).show();
                }
            }
    }
    fun setRegBtn(v: View){

    }
    fun setSubBtn(v: View){
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        finish()
        this.overridePendingTransition(R.anim.global_in, R.anim.global_out);
    }

}