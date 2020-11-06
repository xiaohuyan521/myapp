package com.xh.mian.myapp.tools.uitl;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogUtil {

    private static int mMaxFilesNum = 10;//最大文件数
    private static HandlerThread mWriteThread = null;
    private static int mMaxFiles = 1024*500; //1024*1021*1 = 1m
    private static final String PATCH = "/1/logs";

    private static final boolean ISOPEN = true;//是否开启日志模式
    private static final String TAG = "LogUtil";//默认日志TAG

    public static void initLog(){
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag(TAG)
                //.methodCount(3) // 决定打印多少行（每一行代表一个方法）默认：2
                .build();
        Logger.clearLogAdapters();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }
    private static void fileLog(){
        stopfileLog();
        String folder = SDCardHelper.getSDCardBaseDir()+PATCH;
        ClearLogFiles(folder);

        mWriteThread = new HandlerThread("AndroidFileLogger." + folder);
        mWriteThread.start();
        try {
            //通过反射实例化DiskLogStrategy中的内部类WriteHandler
            Class<?> clazz = Class.forName("com.orhanobut.logger.DiskLogStrategy$WriteHandler");
            Constructor constructor = clazz.getDeclaredConstructor(Looper.class, String.class, int.class);
            //开启强制访问
            constructor.setAccessible(true);
            //核心：通过构造函数，传入相关属性，得到WriteHandler实例
            Handler handler = (Handler) constructor.newInstance(mWriteThread.getLooper(), folder, mMaxFiles);
            //创建缓存策略
            FormatStrategy strategy = CsvFormatStrategy.newBuilder().logStrategy(new DiskLogStrategy(handler)).build();
            DiskLogAdapter adapter = new DiskLogAdapter(strategy);
            Logger.clearLogAdapters();
            Logger.addLogAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            mWriteThread.quit();
        }
    }
    private static void ClearLogFiles(String folder){
        File file = new File(folder);
        if (!file.exists()){
            file.mkdirs();
            return;
        }
        File[] subFile = file.listFiles();
        if (subFile.length <= mMaxFilesNum){
            return;
        }
        int iDelFileNum = subFile.length - mMaxFilesNum;
        Arrays.sort(subFile, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if((o1.lastModified()-o2.lastModified()>0)){
                    return 1;
                }
                return -1;
            }
        });
        for (int i=0;i<iDelFileNum;i++){
            subFile[i].delete();
        }
    }
    private static void stopfileLog(){
        Logger.clearLogAdapters();
        if (null ==mWriteThread){
            return;
        }
        mWriteThread.quit();
        mWriteThread = null;
    }

    public static void v(String message,@Nullable String tag) {
        if(!ISOPEN)return;
        Logger.clearLogAdapters();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder().tag(tag).build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        Logger.v(message);
    }
    public static void v(String tag,String message,Boolean isFile) {
        if(!ISOPEN)return;
        if(isFile){fileLog();}
        v(tag,message);
        if(isFile){stopfileLog();}
    }

    public static void d(String tag,String message) {
        if(!ISOPEN)return;
        Logger.clearLogAdapters();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder().tag(tag).build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        Logger.d(message);
    }
    public static void d(String tag,String message,Boolean isFile) {
        if(!ISOPEN)return;
        if(isFile){fileLog();}
        d(tag,message);
        if(isFile){stopfileLog();}
    }

    public static void i(String message) {
        if(!ISOPEN)return;
        Logger.i(message);
    }
    public static void i(String tag,String message) {
        if(!ISOPEN)return;
        Logger.clearLogAdapters();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder().tag(tag).build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        Logger.i(message);
    }
    public static void i(String tag,String message,Boolean isFile) {
        if(!ISOPEN)return;
        if(isFile){fileLog();}
        i(tag,message);
        if(isFile){stopfileLog();}
    }

    public static void e(String tag,String message,Throwable t) {
        if(!ISOPEN)return;
        Logger.clearLogAdapters();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder().tag(tag).build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        Logger.e(t,message);
    }
    public static void e(String tag,String message,Throwable t,Boolean isFile) {
        if(!ISOPEN)return;
        if(isFile){fileLog();}
        e(tag,message,t);
        if(isFile){stopfileLog();}
    }

    //assert
    public static void wtf(@NonNull String message, @Nullable Object... args) {
        if(!ISOPEN)return;
        Logger.wtf(message, args);
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(@Nullable String json) {
        if(!ISOPEN)return;
        Logger.json(json);
    }
    public static void json(@Nullable String json,Boolean isFile) {
        if(!ISOPEN)return;
        if(isFile){fileLog();}
        Logger.json(json);
        if(isFile){stopfileLog();}
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(@Nullable String xml) {
        if(!ISOPEN)return;
        Logger.xml(xml);
    }
}
