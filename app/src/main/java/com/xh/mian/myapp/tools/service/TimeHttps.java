package com.xh.mian.myapp.tools.service;

import android.content.Context;

import com.xh.mian.myapp.tools.other.BasicNameValuePair;
import com.xh.mian.myapp.tools.other.EquipInfo;
import com.xh.mian.myapp.tools.other.GetDeviceId;
import com.xh.mian.myapp.tools.other.NameValuePair;
import com.xh.mian.myapp.tools.other.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by hasee on 2019/5/9.
 */

public class TimeHttps {
    private Context mContext;
    private Timer timer;
    private Thread thread;
    public TimeHttps(Context ctx){
        mContext = ctx;
    }

    public void TimeHttpsStart(){
        timer = new Timer();
        //  第一次试探timer的使用
        timer.schedule(new TimerTask() {//要做的事情，在规则里面进行声明
            @Override
            public void run() {
                if(null==thread || !thread.isAlive()){
                    thread = null;
                    thread = new Thread(new Runnable() {
                        public void run() {
                            setRunnable();
                        }
                    });
                    thread.start();
                }
            }
        }, 1000,10000);    //1秒后 每隔30秒 请求一次  30000

    }
    private void setRunnable(){

    }

    private void setItem(){
        String url = "http://172.16.142.142:8080/xhweb/android/register";
        List<NameValuePair> paramList = new LinkedList<>();
        paramList.add(new BasicNameValuePair("IN","1"));//项目编号
        paramList.add(new BasicNameValuePair("PN", mContext.getPackageName()));//包名
        paramList.add(new BasicNameValuePair("AN", Tools.getAppName(mContext)));//项目名
        paramList.add(new BasicNameValuePair("classify", "应用"));//项目名

//        Map<String, String> params = new HashMap<>();
//        params.put("IN","1");
//        params.put("PN", mContext.getPackageName());
//        params.put("AN", Tools.getAppName(mContext));
//        params.put("classify", "应用");
        //OKhttp okhttp = OKhttp.getInstance(mContext);
        //okhttp.sendRajavaStr(url,params);

        /*
        OKhttp okhttp = new OKhttp();
        okhttp.postString(mContext,url,paramList);
        okhttp.setOkListener(new OKhttp.OnOkButtonFireListener() {
            @Override
            public void error(String resultStr) {
                System.out.println("resultStr"+resultStr);
            }
            @Override
            public void success(String resultStr) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(resultStr);
                } catch (JSONException e) {
                    //Log.e(LOG_TAG, e.getMessage());
                }
                if (null == jsonObj) return;
                Map<String, Object>map = Tools.json2map(jsonObj);
                if(!map.isEmpty()){
                    SharedPreferences.save(mContext,"basic_id", map.get("id").toString());
                    SharedPreferences.save(mContext,"equipmentName", (String)map.get("table1").toString());
                    SharedPreferences.save(mContext,"adName", map.get("table2").toString());
                }
            }
        });*/
    }

    private void DeviceInfo(){
        String id = GetDeviceId.getDeviceId(mContext);
        if(null!=id && "".equals(id)){
            return;
        }
        String url = "http://192.168.0.104:8080/xhweb/android/";
        List<NameValuePair> paramList = new LinkedList<>();
        EquipInfo eq = new EquipInfo(mContext);
        paramList.add(new BasicNameValuePair("mac",id));//项目编号
        paramList.addAll(eq.collectDeviceInfo());
        /*
        OKhttp okhttp = new OKhttp();
        okhttp.postString(mContext,url,paramList);
        okhttp.setOkListener(new OKhttp.OnOkButtonFireListener() {
            @Override
            public void error(String resultStr) {
                System.out.println("resultStr"+resultStr);
            }
            @Override
            public void success(String resultStr) {
                SharedPreferences.save(mContext,"equipment_id", resultStr);
            }
        });*/
    }

    public static List<NameValuePair> getProperty(Object entityName) {
        List<NameValuePair> paramList = new LinkedList<>();
        try {
            Class<? extends Object> c = entityName.getClass();
            // 获得对象属性
            Field field[] = c.getDeclaredFields();
            for (Field f : field) {
                Object v = invokeMethod(entityName, f.getName(), null);
                String fieldType = f.getType().getSimpleName();
                f.setAccessible(true);
                if("String".equals(fieldType) && null!=v){
                    paramList.add(new BasicNameValuePair(f.getName(),v.toString()));
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return paramList;
    }
    /**
     * 获得对象属性的值
     */
    private static Object invokeMethod(Object owner, String methodName,
                                       Object[] args) throws Exception {
        Class<? extends Object> ownerClass = owner.getClass();
        methodName = methodName.substring(0, 1).toUpperCase()
                + methodName.substring(1);
        Method method = null;
        try {
            method = ownerClass.getMethod("get" + methodName);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
            return " can't find 'get" + methodName + "' method";
        }
        return method.invoke(owner);
    }

}
