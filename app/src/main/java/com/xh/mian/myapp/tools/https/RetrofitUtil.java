package com.xh.mian.myapp.tools.https;

import com.xh.mian.myapp.MyApplication;
import com.xh.mian.myapp.tools.https.Interceptor.CacheInterceptor;
import com.xh.mian.myapp.tools.https.Interceptor.HeaderInterceptor;
import com.xh.mian.myapp.tools.https.Interceptor.LoggingInterceptor;
import com.xh.mian.myapp.tools.https.Interceptor.MyCookieJar;
import com.xh.mian.myapp.tools.https.Interceptor.commonParamsInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitUtil {
    private volatile static RetrofitUtil sInstance;
    private Retrofit mRetrofit;
    public RetrofitUtil(String url){
        try {
            if(url.equals("")){
                url = "http://124.126.120.88:8012/";
            }
            mRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(url)
                    .addConverterFactory(ScalarsConverterFactory.create())//支持获取字符串
                    .addConverterFactory(GsonConverterFactory.create())//支持gson
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//支持Observable
                    .build();
        }catch (Exception e){
            e.getMessage();
        }

    }
    public static RetrofitUtil getInstance(String url){
        if (sInstance == null){
            synchronized(RetrofitUtil.class){
                if (sInstance == null){
                    sInstance = new RetrofitUtil(url);
                }
            }
        }
        return sInstance;
    }

    public static RetrofitUtil getInstance(){
        if (sInstance == null){
            synchronized(RetrofitUtil.class){
                if (sInstance == null){
                    sInstance = new RetrofitUtil("https://www.baidu.com/");
                }
            }
        }
        return sInstance;
    }
    public Retrofit getT(){
        return mRetrofit;
    }
    public RetrofitService getService(){
        return mRetrofit.create(RetrofitService.class);
    }

    public static OkHttpClient okHttpClient;
    public static Long NETWORK_TIME = 5L;//设置请求时长
    public static String CACHE_NAME = "https_cache";//缓存文件名
    public static void initHttps(){
        File cacheDir = new File(MyApplication.instance.getCacheDir(), RetrofitUtil.CACHE_NAME);
        Cache mCache = new Cache(cacheDir, 8 * 1024 * 1024);//8M缓存
        RetrofitUtil.okHttpClient= new OkHttpClient.Builder()
            .connectTimeout(RetrofitUtil.NETWORK_TIME, TimeUnit.SECONDS) //连接超时阈值
            .readTimeout(RetrofitUtil.NETWORK_TIME, TimeUnit.SECONDS) //读超时阈值
            .writeTimeout(RetrofitUtil.NETWORK_TIME, TimeUnit.SECONDS)//写超时阈值
            .retryOnConnectionFailure(true)//当失败后重试
            .addInterceptor(new LoggingInterceptor())//日志拦截器
            .addNetworkInterceptor(new CacheInterceptor())//设置缓存
            .addInterceptor(new CacheInterceptor())//缓存拦截器
            .cache(mCache)
            .build();
    }
    public static void initHttpsAll(){
        //TimeUnit.DAYS天  TimeUnit.HOURS小时 TimeUnit.MINUTES分钟 TimeUnit.SECONDS秒 TimeUnit.MILLISECONDS毫秒
        //Thread.sleep( 5 * 1000 ); = TimeUnit.SECONDS.sleep( 5 );
        //getCacheDir()方法用于获取/data/data/<application package>/cache目录 一般存放临时缓存数据
        //getFilesDir()方法用于获取/data/data/<application package>/files目录 一般放一些长时间保存的数据

        File cacheDir = new File(MyApplication.instance.getCacheDir(), RetrofitUtil.CACHE_NAME);
        Cache mCache = new Cache(cacheDir, 8 * 1024 * 1024);//8M缓存
        RetrofitUtil.okHttpClient= new OkHttpClient.Builder()
            .connectTimeout(RetrofitUtil.NETWORK_TIME, TimeUnit.SECONDS) //连接超时阈值
            .readTimeout(RetrofitUtil.NETWORK_TIME, TimeUnit.SECONDS) //读超时阈值
            .writeTimeout(RetrofitUtil.NETWORK_TIME, TimeUnit.SECONDS)//写超时阈值
            .retryOnConnectionFailure(true)//当失败后重试
            .addInterceptor(new HeaderInterceptor())//请求头拦截器
            .addInterceptor(new commonParamsInterceptor())// 统一请求拦截器
            .addInterceptor(new LoggingInterceptor())//日志拦截器
            .addNetworkInterceptor(new CacheInterceptor())//设置缓存
            .addInterceptor(new CacheInterceptor())//缓存拦截器
            .cookieJar(new MyCookieJar())//设置Cookie
            .cache(mCache)
            .build();
    }
    /**
     * 拦截器Interceptors
     * 通过响应拦截器实现了从响应中获取服务器返回的time
     *
     * @return
     */
    public static Interceptor getResponseHeader() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response response = chain.proceed(chain.request());
                String timestamp = response.header("time");
                if (timestamp != null) {
                    //获取到响应header中的time
                }
                return response;
            }
        };
        return interceptor;
    }

}
