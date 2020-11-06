package com.xh.mian.myapp.tools.https.Interceptor;

import com.xh.mian.myapp.MyApplication;
import com.xh.mian.myapp.tools.other.Tools;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
/**
 * 在无网络的情况下读取缓存，有网络的情况下根据缓存的过期时间重新请求,
 * @return
 */
public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!Tools.isNetworkAvailable(MyApplication.instance)) {
            //无网络下强制使用缓存，无论缓存是否过期,此时该请求实际上不会被发送出去。
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
        }
        okhttp3.Response response = chain.proceed(request);
        if (Tools.isNetworkAvailable(MyApplication.instance)) {//有网络情况下，根据请求接口的设置，配置缓存。
            //这样在下次请求时，根据缓存决定是否真正发出请求。
            String cacheControl = request.cacheControl().toString();
            //当然如果你想在有网络的情况下都直接走网络，那么只需要
            //将其超时时间这是为0即可:String cacheControl="Cache-Control:public,max-age=0"
            int maxAge = 60 * 60; // read from cache for 1 minute
            return response.newBuilder()
                            //.header("Cache-Control", cacheControl)
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .removeHeader("Pragma")
                    .build();
        } else {
            //无网络
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            return response.newBuilder()
                    .header("Cache-Control", "public,only-if-cached,max-stale=" + maxStale)
                    .removeHeader("Pragma")
                    .build();
        }
    }
}

