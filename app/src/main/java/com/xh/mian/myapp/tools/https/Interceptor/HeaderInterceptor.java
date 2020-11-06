package com.xh.mian.myapp.tools.https.Interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 拦截器Interceptors
 * 使用addHeader()不会覆盖之前设置的header,若使用header()则会覆盖之前的header
 *
 * @return
 */
public class HeaderInterceptor  implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();
        builder.addHeader("version", "1");
        builder.addHeader("time", System.currentTimeMillis() + "");
        Request.Builder requestBuilder = builder.method(originalRequest.method(), originalRequest.body());
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}

