package com.xh.mian.myapp.tools.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.xh.mian.myapp.tools.other.Location;


/**
 * Created by hasee on 2018/12/13.
 */

public class AppService extends IntentService {
    //
    /**
        Intent i = new Intent(context, AppService.class);
        i.putExtra("finish","over");
        i.setAction(AppService.ACTION_MSGFINSIH1);
        //开启服务
        context.startService(i);
        //关闭服务
        //context.stopService(i);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //回调
                String action = intent.getAction();
                String s = intent.getStringExtra("msg");
                if (null != s) {
                    System.out.println("msg:"+s);
                }
            }
        };

        //注册接收  开启接收回调
        LocalBroadcastManager.getInstance(context).registerReceiver((receiver),
                new IntentFilter(AppService.ACTION_MSGTEXT1));

        //取消注册
        //LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    */
    private String LOG_TAG = "AppService";
    public static final String ACTION_MSGSEND="com.mylibrary.services.ACTION_MSGSEND";
    public static final String ACTION_MSG1="com.mylibrary.services.ACTION_MSG1";
    public static final String ACTION_MSG2="com.mylibrary.services.ACTION_MSG2";

    private LocalBroadcastManager broadcaster;
    public AppService(String name) {
        super(name);
    }
    public AppService() {
        super("AppService");
    }
    @Override
    public IBinder onBind(Intent arg0) {
        Log.v(LOG_TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.v(LOG_TAG, "onCreate");
        broadcaster =  LocalBroadcastManager.getInstance(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOG_TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        Log.v(LOG_TAG, "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MSG1.equals(action)) {
                Location location= new Location(this);
                location.setOkListener(new Location.OnOkButtonFireListener() {
                    @Override
                    public void error() {
                        Intent intent = new Intent(ACTION_MSGSEND);
                        intent.setAction(AppService.ACTION_MSG1);
                        intent.putExtra("msg","error");
                        broadcaster.sendBroadcast(intent);
                    }
                    @Override
                    public void success() {
                    }
                });
                location.initLocation();
            }

            if (ACTION_MSG2.equals(action)) {
                String param1 = intent.getStringExtra("finish");
                Intent intent1 = new Intent(ACTION_MSGSEND);
                intent1.putExtra("msg",param1+",IntentService收到并且广播了出去");
                broadcaster.sendBroadcast(intent1);
            }
        }
    }
}
