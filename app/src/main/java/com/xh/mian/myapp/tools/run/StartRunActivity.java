package com.xh.mian.myapp.tools.run;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.xh.mian.myapp.tools.service.AppService;
import com.xh.mian.myapp.tools.service.TimeHttps;

/**
 * Created by hasee on 2019/5/9.
 *
 */

public class StartRunActivity extends Activity {
    private BroadcastReceiver receiver;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //开启服务 获取位置
        Intent i = new Intent(this, AppService.class);
        i.putExtra("finish","over");
        i.setAction(AppService.ACTION_MSG1);
        startService(i);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //回调
                String action = intent.getAction();
                if(action.contains(AppService.ACTION_MSG1)){
                    String s = intent.getStringExtra("msg");
                    if (null != s && !"".equals(s) && "error".equals(s)) {
                        intent.setClass(context, AppService.class);
                        intent.setAction(AppService.ACTION_MSG1);
                        context.startService(intent);
                    }
                }
                if(action.contains(AppService.ACTION_MSG2)){

                }
            }
        };

        //注册接收  开启接收回调
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(AppService.ACTION_MSGSEND));

        //提交信息数据
        TimeHttps http = new TimeHttps(this);
        http.TimeHttpsStart();
    }





}
