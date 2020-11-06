package com.xh.mian.myapp.tools.https;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitService<T> {
    /**
     * 获取新闻  使用rxjava
     * @return
     */
    @POST("url")
    Observable<T> getNewsWithRxJava(@Query("key") String key, @Query("type") String type);

    Observable<String> getWithRxJava(@PartMap Map<String, String> params);

    @Multipart
    @POST("url")    // url:请求地址
    Call<ResponseBody> testPostFormData(@PartMap Map<String, RequestBody> params);
    // 构建请求参数
    //Map<String, RequestBody> map = new HashMap<>();
    //map.put("param_1", RequestBody.create(MediaType.parse("text/plain"), "param1")); // param_1：参数名；param1：参数值
    //map.put("param_2", RequestBody.create(MediaType.parse("text/plain"), "param2"));

    /**
     * 获取新闻  不使用rxjava
     * @return
     */
    @POST("http://124.126.120.88:8012/api/access-token")
    Call<ResponseBody> getNewsWithoutRxJava(@Query("secretKey") String name, @Query("appKey") String passwd);

    @POST("http://124.126.120.88:8012/api/access-token")
    Observable<NewsBean> getWithoutRxJava(@Query("secretKey") String name, @Query("appKey") String passwd);


    @GET()
    Observable<String> getWithoutRxJava(@Url String url);
}
/*
@Url 替换url
@QueryMap  替换url中查询参数
@Header  替换header
@FieldMap 替换post请求body中参数
@FormUrlEncoded post请求需要加的方法注解
@POST() 标示该方法为post请求
@GET() 标示该方法为get请求

@FormUrlEncoded 表示请求体是一个 Form 表单.
@Multipart 表示请求体是一个支持文件上传的表单.
Streaming表示返回的数据全部用流的形式返回, 如果不使用它, 默认会把全部数据加载到内存, 所以下载大文件时需要加上这个注解.
 */