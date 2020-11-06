package com.xh.mian.myapp.appui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.xh.mian.myapp.R
import com.xh.mian.myapp.appui.main.MainActivity
import com.xh.mian.myapp.tools.db.DbHelper
import com.xh.mian.myapp.tools.db.SharedPreferences

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        SharedPreferences.save("welcome",true)
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