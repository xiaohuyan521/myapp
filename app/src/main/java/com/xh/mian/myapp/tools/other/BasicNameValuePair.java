package com.xh.mian.myapp.tools.other;

/**
 * Created by zhangcheng on 2017/8/1.
 */

public class BasicNameValuePair implements  NameValuePair{

    String mName ="";
    String mValue = "";

    public BasicNameValuePair(String name, String value) {
        mName = name;
        mValue = value;
    }

    public String getName() {
        return mName;
    }

    public String getValue() {
        return mValue;
    }

    public String toString() {
       return mName+"="+mValue;
    }


}
