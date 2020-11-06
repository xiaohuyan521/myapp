package com.xh.mian.myapp.tools.other;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.xh.mian.myapp.tools.db.SharedPreferences;
import com.xh.mian.myapp.tools.https.HttpUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by hasee on 2019/6/11.
 */

public class Location {
    private Context mContext;
    public Location(Context mContext) {
            this.mContext = mContext;
    }
    public void initLocation(){
        try{
            LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);//获得位置服务
            String provider = judgeProvider(locationManager);

            if (provider != null) {//有位置提供器的情况
                //为了压制getLastKnownLocation方法的警告
                if (Build.VERSION.SDK_INT>=23){
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        SharedPreferences.save("location", "无访问位置权限");
                        okListener.error();
                        return;
                    }
                }
                android.location.Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    getLocation(location);//得到当前经纬度并开启线程去反向地理编码
                } else {
                    locationManager.requestLocationUpdates(provider, 1000, 1,  new LocationListener(){
                        @Override
                        public void onLocationChanged(final android.location.Location location) {
                            getLocation(location);
                        }
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }
                        @Override
                        public void onProviderEnabled(String provider) {

                        }
                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
                    return ;
                }

            }else{//不存在位置提供器的情况
                SharedPreferences.save("location", "不存在位置提供器");
                okListener.error();
                return ;
            }
        }catch (Exception e){
            SharedPreferences.save("location", e.getMessage().toString());
            okListener.error();
            return ;
        }
    }
    /**
     * 判断是否有可用的内容提供器
     * @return 不存在返回null
     */
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if(prodiverlist.contains(LocationManager.GPS_PROVIDER)){
            return LocationManager.GPS_PROVIDER;
        }else if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        }else{
            //Toast.makeText(ShowLocation.this,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 得到当前经纬度并开启线程去反向地理编码
     */
    private void getLocation(final android.location.Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String latitude = location.getLatitude()+"";
                String longitude = location.getLongitude()+"";
                final String url = "http://api.map.baidu.com/geocoder/v2/?ak=xsKhhXMQG4rd3ZzQ34vMKje5&callback=renderReverse&location="+latitude+","+longitude+"&output=json&pois=0";
                HttpUrl http = new HttpUrl();
                String resultStr = http.postString(url,"");
                try {
                    resultStr = resultStr.replace("renderReverse&&renderReverse","");
                    resultStr = resultStr.replace("(","");
                    resultStr = resultStr.replace(")","");
                    JSONObject jsonObject = new JSONObject(resultStr);
                    JSONObject address = jsonObject.getJSONObject("result");
                    String city = address.getString("formatted_address");
                    String district = address.getString("sematic_description");
                    String str = city+district;
                    SharedPreferences.save("location", str);
                    //if(null!=StartRunActivity.adpo){
                    //    StartRunActivity.adpo.setPosition(str);
                    //    SharedPreferences.save(mContext,"ad_id", "");
                    //}
                    okListener.success();
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences.save("location", "获取失败");
                okListener.error();
                return;
            }
        }).start();
    }

    //设置一个接口 提供外部数据 类似handle功能
    private OnOkButtonFireListener okListener;
    public interface OnOkButtonFireListener{
        public void error();
        public void success();
    }
    public void setOkListener(OnOkButtonFireListener okListener) {
        this.okListener = okListener;
    }
}
