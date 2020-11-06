package com.xh.mian.myapp.tools.https;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

import com.xh.mian.myapp.tools.dialog.LoadingDialog;
import com.xh.mian.myapp.tools.uitl.LogUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

//https://www.cnblogs.com/shenchanghui/p/8964754.html 参考文献
public class OKhttp <T>  {
    private static final String TAG = "OKhttp";
    private volatile static OKhttp sInstance;
    public Context mContext;
    public static OKhttp getInstance(Context _mContext){
        if (sInstance == null){
            synchronized(OKhttp.class){
                if (sInstance == null){
                    sInstance = new OKhttp();
                    sInstance.mContext = _mContext;
                }
            }
        }
        return sInstance;
    }

    public void sendCal(String url){
        long time1 = System.currentTimeMillis();
        Call<ResponseBody> call = RetrofitUtil.getInstance(url).getService()
                .getNewsWithoutRxJava("8bf17cf1c321723f060d5dc5c4da871a", "top");
        long time2 = System.currentTimeMillis();
        LogUtil.i("MainActivity", "请求耗时：" + (time2 - time1) + "ms");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    LogUtil.i("MainActivity", "Thread.currentThread():" + Thread.currentThread());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendRajava() {
        final LoadingDialog loadingDialog = new LoadingDialog(mContext, "正在加载数据...");
        loadingDialog.showDialog();
        RetrofitService<NewsBean> mTestService = RetrofitUtil.getInstance("").getT().create(RetrofitService.class);
        mTestService.getWithoutRxJava("https://www.baidu.com/")//"kfapp","KFAPP*asr191109"
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<String>() {
                private Disposable mDisposable;//貌似是处理内存泄漏 或终止线程用的
                @Override
                public void onSubscribe(Disposable d) {
                    mDisposable = d;
                }
                @Override
                public void onNext(String value) {
                    Toast.makeText(mContext, value.toString(), Toast.LENGTH_SHORT).show();
                    okListener.onMessage(value.toString());
                    if(null!=loadingDialog)
                        loadingDialog.stopDialog();
                    if (mDisposable != null && !mDisposable.isDisposed())
                        mDisposable.dispose();//注销
                }
                @Override
                public void onError(Throwable e) {
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    okListener.onError();
                    if(null!=loadingDialog)
                        loadingDialog.stopDialog();
                    if (mDisposable != null && !mDisposable.isDisposed())
                        mDisposable.dispose();//注销
                }
                @Override
                public void onComplete() {
                    if(null!=loadingDialog)
                        loadingDialog.stopDialog();
                    if (mDisposable != null && !mDisposable.isDisposed())
                        mDisposable.dispose();
                }
            });
    }
    public OnOkButtonFireListener okListener;
    public interface OnOkButtonFireListener{
        public void onError();
        public void onMessage(String resultStr);
    }
    public void setOkListener(OnOkButtonFireListener okListener) {
        this.okListener = okListener;
    }

    //https://juejin.im/post/6844903617124630535
    public void setrxjava(){
        Observable.create(new ObservableOnSubscribe< Integer >() {
            @Override
            public void subscribe(ObservableEmitter< Integer > e) throws Exception {
                LogUtil.d(TAG, "=========================currentThread name: " + Thread.currentThread().getName());
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
                /*
                onNext()
                发送该事件时，观察者会回调 onNext() 方法
                onError()
                发送该事件时，观察者会回调 onError() 方法，当发送该事件之后，其他事件将不会继续发送
                onComplete()
                发送该事件时，观察者会回调 onComplete() 方法，当发送该事件之后，其他事件将不会继续发送
                */
            }
        }).subscribe(new Observer < Integer > () {
            private Disposable mDisposable;
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
                LogUtil.d(TAG, "======================onSubscribe");
            }
            @Override
            public void onNext(Integer integer) {
                LogUtil.d(TAG, "======================onNext " + integer);
                mDisposable.dispose();
            }
            @Override
            public void onError(Throwable e) {
                LogUtil.d(TAG, "======================onError");
                mDisposable.dispose();
            }
            @Override
            public void onComplete() {
                LogUtil.d(TAG, "======================onComplete");
                mDisposable.dispose();
            }
        });


        /*
        =================onNext 1
        =================onNext 2
        =================onNext 3
        =================onComplete
         */
        //Integer array[] = {1, 2, 3, 4};
        //Observable.fromArray(array)

        Observable.just(1, 2, 3)
            .subscribe(new Observer < Integer > () {
                @Override
                public void onSubscribe(Disposable d) {
                    LogUtil.d(TAG, "=================onSubscribe");
                }

                @Override
                public void onNext(Integer integer) {
                    LogUtil.d(TAG, "=================onNext " + integer);
                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.d(TAG, "=================onError ");
                }

                @Override
                public void onComplete() {
                    LogUtil.d(TAG, "=================onComplete ");
                }
            });
    }
}
