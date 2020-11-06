package com.xh.mian.myapp.tools.https.Interceptor;

import com.xh.mian.myapp.tools.https.HttpUrl;
import com.xh.mian.myapp.tools.uitl.LogUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        LogUtil.i("OkHttp", "HttpHelper1" + String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        okhttp3.Response response = chain.proceed(request);
        long t2 = System.nanoTime();

        LogUtil.i("OkHttp", "HttpHelper2" + String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        return response;
    }
}