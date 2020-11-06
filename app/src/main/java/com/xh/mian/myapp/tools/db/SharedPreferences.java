package com.xh.mian.myapp.tools.db;

import android.content.Context;

import com.xh.mian.myapp.tools.other.Tools;

public class SharedPreferences {

    private final static String SAVE_PATCH = "savepatch";
    private static android.content.SharedPreferences.Editor editor; //存储控制器
    private static android.content.SharedPreferences pref;

    //Context赋值
    public static void initSharePreferences(Context context) {
        pref = context.getSharedPreferences(Tools.getAppName(context)+SAVE_PATCH, Context.MODE_PRIVATE);
        //存储控制器
        editor = pref.edit();
    }

    public static void initSharePreferences(Context context,String patch) {
        pref = context.getSharedPreferences(patch, Context.MODE_PRIVATE);
        //存储控制器
        editor = pref.edit();
    }

    public static void save(String Name, Object param) {
        if (param instanceof Integer) {
            int value = ((Integer) param).intValue();
            editor.putInt(Name, value);
        } else if (param instanceof String) {
            String s = (String) param;
            editor.putString(Name,s);
        } else if (param instanceof Float) {
            float f = ((Float) param).floatValue();
            editor.putFloat(Name,f);
        } else if (param instanceof Boolean) {
            boolean b = ((Boolean) param).booleanValue();
            editor.putBoolean(Name,b);
        }
        editor.commit();
    }

    public static String getString(String Name) {
        String uid = pref.getString(Name, "");
        return uid;

    }
    public static int getInt(String Name) {
        int uid = pref.getInt(Name, 0);
        return uid;
    }
    public static Boolean getBoolean(String Name) {
        Boolean uid = pref.getBoolean(Name, false);
        return uid;
    }
    public static float getFloat(String Name) {
        float uid = pref.getFloat(Name, 0.0f);
        return uid;

    }
}
