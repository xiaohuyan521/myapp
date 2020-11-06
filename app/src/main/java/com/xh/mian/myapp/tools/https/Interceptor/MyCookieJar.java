package com.xh.mian.myapp.tools.https.Interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class MyCookieJar implements CookieJar {
    final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
    @Override
    public void saveFromResponse(okhttp3.HttpUrl url, List<Cookie> cookies) {
        cookieStore.put(url, cookies);//保存cookie
    }
    @Override
    public List<Cookie> loadForRequest(okhttp3.HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);//取出cookie
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
}
