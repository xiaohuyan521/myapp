package com.xh.mian.myapp.tools.other;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.UUID;

/**
 * @author liangjun on 2018/1/21.
 */

public class GetDeviceId {

    /**
     * 获取设备唯一标识符
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = "";
        try {
            //获取IMES(也就是常说的DeviceId)
            deviceId = getIMIEStatus(context);
            if(null!=deviceId && !"".equals(deviceId))
            return "IMES="+deviceId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //获取设备的MACAddress地址 去掉中间相隔的冒号
            deviceId = getLocalMac(context);
            if(null!=deviceId && !"".equals(deviceId))
            return "MAC="+deviceId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //如果以上搜没有获取相应的则自己生成相应的UUID作为相应设备唯一标识符
            UUID uuid = UUID.randomUUID();
            deviceId = uuid.toString();
            if(null!=deviceId && !"".equals(deviceId))
            return "UUID="+deviceId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deviceId;
    }




    /**
     * 获取设备的DeviceId(IMES) 这里需要相应的权限<br/>
     * 需要 READ_PHONE_STATE 权限
     *
     * @param context
     * @return
     */
    private static String getIMIEStatus(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        return deviceId;
    }


    /**
     * 获取设备MAC 地址 由于 6.0 以后 WifiManager 得到的 MacAddress得到都是 相同的没有意义的内容
     * 所以采用以下方法获取Mac地址
     * @param context
     * @return
     */
    private static String getLocalMac(Context context) {
        /*
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
        */

        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "";
            }
            byte[] addr = networkInterface.getHardwareAddress();


            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        }
        return macAddress;
    }

}