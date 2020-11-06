package com.xh.mian.myapp;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import androidx.multidex.MultiDex;

import com.xh.mian.myapp.tools.db.SharedPreferences;
import com.xh.mian.myapp.tools.https.RetrofitUtil;
import com.xh.mian.myapp.tools.uitl.LogUtil;


public class MyApplication extends Application {
    public static MyApplication instance;
    public Boolean isdbk = false;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        isdbk = false;
        // 调用相机加权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        //初始化储存器
        SharedPreferences.initSharePreferences(getApplicationContext());

        LogUtil.initLog();

        RetrofitUtil.initHttps();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }


}
