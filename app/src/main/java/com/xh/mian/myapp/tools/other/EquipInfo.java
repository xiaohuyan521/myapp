package com.xh.mian.myapp.tools.other;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

import com.xh.mian.myapp.tools.db.SharedPreferences;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hasee on 2018/11/1.
 */

public class EquipInfo {
    private Context mContext;

    public EquipInfo(Context mContext) {
        this.mContext = mContext;
    }

    public List<NameValuePair> collectDeviceInfo() {
        List<NameValuePair> paramList = new LinkedList<>();
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                paramList.add(new BasicNameValuePair("sdk_version", pi.versionName));
                paramList.add(new BasicNameValuePair("app_version", String.valueOf(pi.versionCode)));
            }
            paramList.add(new BasicNameValuePair("position", SharedPreferences.getString("location")));
            paramList.add(new BasicNameValuePair("hardware", getDeviceInfo()));
        } catch (Exception e) {
            e.getMessage();
        }
        return paramList;
    }

    private String getDeviceInfo() {
        StringBuffer sb = new StringBuffer();
        try {
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            int w =dm.widthPixels;
            int h = dm.heightPixels;
            double dc = getPingMuSize(mContext);
            double dp = Tools.getScreen(w,h,dc);
            sb.append("Android版本号：" + Build.VERSION.SDK_INT);
            sb.append("\nscreenWidth：" + w);
            sb.append("\nscreenHeight：" + h);
            sb.append("\n屏幕尺寸：" + dc);
            sb.append("\ndip：" + dp);
            sb.append("\n主板：" + Build.BOARD);
            sb.append("\n系统定制商：" + Build.BRAND);
            sb.append("\ncpu指令集：" + Build.CPU_ABI);
            sb.append("\n设置参数：" + Build.DEVICE);
            sb.append("\n显示屏参数：" + Build.DISPLAY);
            sb.append("\n硬件识别码：" + Build.FINGERPRINT);
            sb.append("\n硬件名称：" + Build.HARDWARE);
            sb.append("\n硬件制造商：" + Build.MANUFACTURER);
            sb.append("\n手机型号：" + Build.MODEL);
            sb.append("\n硬件序列号：" + Build.SERIAL);
            sb.append("\n手机制造商：" + Build.PRODUCT);

        } catch (Exception e) {
            e.getMessage();
        }

        return sb.toString();
    }
    /**
     * @ 获取当前手机屏幕尺寸
     */
    private float getPingMuSize(Context mContext) {
        float xdpi = mContext.getResources().getDisplayMetrics().xdpi;
        float ydpi = mContext.getResources().getDisplayMetrics().ydpi;
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        // 这样可以计算屏幕的物理尺寸
        float width2 = (width / xdpi)*(width / xdpi);
        float height2 = (height / ydpi)*(width / xdpi);
        return (float) Math.sqrt(width2+height2);
    }
}

