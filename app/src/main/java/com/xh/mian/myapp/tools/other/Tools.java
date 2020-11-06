package com.xh.mian.myapp.tools.other;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xh.mian.myapp.R;
import com.xh.mian.myapp.tools.uitl.LogUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class Tools {
    //检测是否联网
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo activeNet = connectivity.getActiveNetworkInfo();
            Log.v("netAvail", "activeNet=" + activeNet);
            if (null == activeNet) {
                return false;
            } else {
                int actNetType = activeNet.getType();
                if (ConnectivityManager.TYPE_WIFI != actNetType
                        && ConnectivityManager.TYPE_MOBILE != actNetType) {
                    return false;
                }
            }
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            int availNetCnt = 0;
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    int netType = info[i].getType();
                    if (ConnectivityManager.TYPE_WIFI == netType
                            || ConnectivityManager.TYPE_MOBILE == netType) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            availNetCnt++;
                        }
                    }

                }
            }
            if (0 < availNetCnt)
                return true;
        }
        return false;
    }
    //根据像素和屏幕尺寸计算dip//屏幕尺寸
    public static double getScreen(int w,int h,double sc){
        double area=Math.sqrt(w*w+h*h);
        double ppi=area/sc;
        double ppiInDP=160;
        if(ppi<160)ppiInDP=160;
        else if(ppi<240)ppiInDP=240;
        else if(ppi<320)ppiInDP=320;
        else if(ppi<480)ppiInDP=480;
        double dp=160*h/ppiInDP;
        return dp;
        //System.out.println("ppi="+ppi+",dp="+dp);
    }
    public static String getAppName(Context context){
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    @SuppressLint("CheckResult")
    public static void getPremissions(Activity context, String...premissions){
        RxPermissions rxPermissions = new RxPermissions(context);
        rxPermissions.request(premissions)
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        Log.d("##", "accept: 获得了权限");
                    } else {
                        //有一个权限未获得就会执行此方法
                        Log.d("##", "accept: 未获得全部权限");
                    }
                }
            });
    }
    private static void getPremissions(final Activity context){
        RxPermissions rxPermissions = new RxPermissions(context);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe(new Observer<Boolean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }
                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean) {
                        Matisse.from(context)
                            .choose(MimeType.ofAll())
                            .countable(true)
                            .maxSelectable(4)
                            .gridExpectedSize(context.getResources().getDimensionPixelSize(R.dimen.dp100))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new MyGlideEngine())
                            .forResult(1);
                    } else {
                        Toast.makeText(context, "权限被拒绝了..", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onError(Throwable e) {

                }
                @Override
                public void onComplete() {

                }
            });
    }
    public static void hideKeyboard(Activity act) {
        InputMethodManager imm = ((InputMethodManager) act
                .getSystemService(Context.INPUT_METHOD_SERVICE));

        imm.hideSoftInputFromWindow(act.findViewById(android.R.id.content)
                .getWindowToken(), 0);

    }
    public static void hideKeyboard(Context ctx, View v) {
        InputMethodManager imm = ((InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE));

        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    public static void appendEditTextListRec(ViewGroup srcGroup,List<EditText> list) {
        int childCnt = srcGroup.getChildCount();
        if (0 == childCnt)
            return;
        for (int i = 0; i < childCnt; i++) {
            View vc = srcGroup.getChildAt(i);
            if (vc instanceof EditText) {
                EditText edt = (EditText) vc;
                list.add(edt);
            } else if (vc instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) vc;
                appendEditTextListRec(vg, list);
            }
        }
    }
    public static Bitmap ratio(Bitmap image,float pixelW) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inSampleSize = (int) (image.getWidth() / pixelW);//设置缩放比例
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        return bitmap;
    }

    /**
     * 网页解析
     * @param data 网页源代码
     * @return 数据数组
     * @throws IOException
     */
    public synchronized static Map<String,String> dataFilter(String data) throws IOException {

        Map<String,String> map = new HashMap<>();
        Document doc =  Jsoup.parse(data);
        String title = doc.getElementsByTag("title").first().text();
        map.put("title",title);

        Elements imags = doc.getElementsByTag("img");
        for (Element aElement : imags){
            String img = aElement.attr("src");
            if(!"".equals(img) && (img.contains(".png")||img.contains(".jpg")) && (img.contains("http")||img.contains("//www"))){
                if(img.contains("//www") && !img.contains("http")){
                    img = "https:"+img;
                }
                map.put("thumbnail",img);
                break;
            }
        }
        Elements aElements = doc.getElementsByTag("a");
        for (Element aElement : aElements){
            String a = aElement.text();
            if(!"".equals(a) && !map.containsKey("desc")){
                map.put("desc",a);
            }else if(!"".equals(a) && !map.containsKey("author")){
                map.put("author",a);
            }else if(!"".equals(a) && !map.containsKey("chapter")){
                map.put("chapter",a);
            }else if(!"".equals(a)){
                break;
            }
        }
        Elements links = doc.select("body");
        for (Element element : links) {
            String time= element.getElementsByClass("time").text();
            if(!"".equals(time)){
                map.put("date",time);
            }
        }

        //getElementsByTag 选择标签
        // getElementById选择ID
        // getElementsByClass 选择class
        // attr 选择节点
        // Elements postItems  for (Element postItem : postItems)

        // 通过标签company查找元素 Elements company = doc.select("company");
        // 带有href属性的a元素 Elements links = doc.select("a[href]");
        // 扩展名为.png的图片 Elements pngs = doc.select("img[src$=.png]");
        // class等于content的div标签 Element content = doc.select("div.content").first();
        return map;
    }




}
