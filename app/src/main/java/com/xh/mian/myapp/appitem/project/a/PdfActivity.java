package com.xh.mian.myapp.appitem.project.a;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.github.barteksc.pdfviewer.PDFView;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xh.mian.myapp.BuildConfig;
import com.xh.mian.myapp.MyFileProvider;
import com.xh.mian.myapp.R;
import com.xh.mian.myapp.tools.db.SharedPreferences;
import com.xh.mian.myapp.tools.dialog.LoadingDialog;
import com.xh.mian.myapp.tools.other.MyGlideEngine;
import com.xh.mian.myapp.tools.other.Tools;
import com.xh.mian.myapp.tools.uitl.EmailUtil;
import com.xh.mian.myapp.tools.uitl.SDCardHelper;
import com.xh.mian.myapp.tools.uitl.Uri2BitmapUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;

public class PdfActivity extends AppCompatActivity {
    private final int REQUESTCODE_PREVIEW = 1;
    private final int REQUESTCODE_MERGE = 2;
    private final int REQUESTCODE_EXPORT = 3;
    private final int REQUESTCODE_SHARE = 4;
    private final String pdfpatch = "/1/pdf/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_view);

    }
    //预览
    @SuppressLint("CheckResult")
    public void setbut1(View v){
        RxPermissions rxPermissions = new RxPermissions(PdfActivity.this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) {
                    if (aBoolean) {
                        //默认打开文件路径
                        String sdcardPath = SDCardHelper.getSDCardBaseDir() + pdfpatch;
                        File destDir = new File(sdcardPath);
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                        }
                        new LFilePicker()
                            .withActivity(PdfActivity.this)
                            .withRequestCode(REQUESTCODE_PREVIEW)
                            .withIconStyle(Constant.ICON_STYLE_BLUE)
                            //.withFileFilter(new String[]{".mp3", ".wav"})
                            .withMaxNum(1)
                            .withTitle("") //标题文字
                            .withMutilyMode(false) //true 多选
                            .withStartPath(sdcardPath)
                            .withIsGreater(false) //过滤文件大小 小于指定大小的文件
                            .withFileSize(200 * 1024 * 1024) //指定文件大小为150M
                            .start();
                    } else {
                        //有一个权限未获得就会执行此方法
                        //Log.d("##", "accept: 未获得全部权限");
                    }
                }
            });
    }
    //生成pdf
    @SuppressLint("CheckResult")
    public void setbut2(View v){
        RxPermissions rxPermissions = new RxPermissions(PdfActivity.this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean)throws Exception {
                    if (aBoolean) {
                        //Log.d("##", "accept: 获得了权限");
                        Matisse.from(PdfActivity.this)
                            .choose(MimeType.ofAll())
                            .countable(true)
                            .maxSelectable(50)
                            .gridExpectedSize(PdfActivity.this.getResources().getDimensionPixelSize(R.dimen.dp100))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new MyGlideEngine())
                            .forResult(REQUESTCODE_MERGE);
                    } else {
                        //有一个权限未获得就会执行此方法
                        //Log.d("##", "accept: 未获得全部权限");
                    }
                }
            });
    }

    //导出
    @SuppressLint("CheckResult")
    public void setbut3(View v){
        RxPermissions rxPermissions = new RxPermissions(PdfActivity.this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) {
                if (aBoolean) {
                    //默认打开文件路径
                    Toast.makeText(getApplicationContext(), "请先选中一个pdf", Toast.LENGTH_SHORT).show();
                    String sdcardPath = SDCardHelper.getSDCardBaseDir() + pdfpatch;
                    File destDir = new File(sdcardPath);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }
                    new LFilePicker()
                        .withActivity(PdfActivity.this)
                        .withRequestCode(REQUESTCODE_EXPORT)
                        .withIconStyle(Constant.ICON_STYLE_BLUE)
                        //.withFileFilter(new String[]{".mp3", ".wav"})
                        .withMaxNum(1)
                        .withMutilyMode(false) //true 多选
                        .withStartPath(sdcardPath)
                        .withIsGreater(false) //过滤文件大小 小于指定大小的文件
                        .withFileSize(200 * 1024 * 1024) //指定文件大小为150M
                        .start();
                } else {
                    //有一个权限未获得就会执行此方法
                    //Log.d("##", "accept: 未获得全部权限");
                }
                }
            });
    }

    //分享
    @SuppressLint("CheckResult")
    public void setbut4(View v){

        RxPermissions rxPermissions = new RxPermissions(PdfActivity.this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) {
                if (aBoolean) {
                    //默认打开文件路径
                    Toast.makeText(getApplicationContext(), "请先选中一个pdf", Toast.LENGTH_SHORT).show();
                    String sdcardPath = SDCardHelper.getSDCardBaseDir() + pdfpatch;
                    File destDir = new File(sdcardPath);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }
                    new LFilePicker()
                        .withActivity(PdfActivity.this)
                        .withRequestCode(REQUESTCODE_SHARE)
                        .withIconStyle(Constant.ICON_STYLE_BLUE)
                        //.withFileFilter(new String[]{".mp3", ".wav"})
                        .withMaxNum(1)
                        .withMutilyMode(false) //true 多选
                        .withStartPath(sdcardPath)
                        .withIsGreater(false) //过滤文件大小 小于指定大小的文件
                        .withFileSize(200 * 1024 * 1024) //指定文件大小为150M
                        .start();
                } else {
                    //有一个权限未获得就会执行此方法
                    //Log.d("##", "accept: 未获得全部权限");
                }
                }
            });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        try {
            if(requestCode == REQUESTCODE_PREVIEW){
                List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                //Toast.makeText(getApplicationContext(), "选中了" + list.size() + "个文件", Toast.LENGTH_SHORT).show();
                //String path = data.getStringExtra("path");
                setPDF(list.get(0));
            }
            if (requestCode == REQUESTCODE_MERGE) {
                List<Uri> mSelected = Matisse.obtainResult(data);
                String[] imageFolderPath = new String[mSelected.size()];
                for (int i = 0; i < mSelected.size(); i++) {
                    Uri uri = mSelected.get(i);
                    imageFolderPath[i]= Uri2BitmapUtil.getFromUri(getBaseContext(), uri);
                }
                setpdf(imageFolderPath);
            }
            if (requestCode == REQUESTCODE_EXPORT) {
                List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
                String name = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
                EmailUtil email = new EmailUtil(this,list.get(0),"871096605@qq.com",
                        "pdf_"+name,
                        this.getPackageName());
                email.sendmial("pdf正在发送中");
                email.setOkListener(new EmailUtil.OnOkButtonFireListener() {
                    @Override
                    public void success() {
                        Toast.makeText(getApplicationContext(), "邮件已发送成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (requestCode == REQUESTCODE_SHARE) {
                List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
                shareFile( list.get(0));
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    private void setPDF(String path){
        try {
            PDFView pdf = (PDFView) findViewById(R.id.pdf);
            File excelFile = new File(path);
            //pdf.setMinZoom(1.5f);
            //pdf.resetZoom();
            pdf.fromFile(excelFile)
                //.pages(0, 2, 3, 4, 5); // 把0 , 2 , 3 , 4 , 5 过滤掉
                //是否允许翻页，默认是允许翻页
                .enableSwipe(true)
                //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                .swipeVertical(true)
                .enableDoubletap(false)
                //设置默认显示第0页
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .password(null).load();
        }catch (Exception e){
            e.getMessage();
        }
    }
    @SuppressLint("StaticFieldLeak")
    private void setpdf(final String[] imageFolderPath){
        final LoadingDialog loadingDialog = new LoadingDialog(PdfActivity.this, "正在合成pdf...");
        loadingDialog.showDialog();
        AsyncTask<String, String, Integer> getTypeAsyncTask = new AsyncTask<String, String, Integer>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
            @Override
            protected void onProgressUpdate(String... values) {
                //progressSync.setMessage(values[0].toString());
                //progressSync.setMessage(values[1].toString());
                super.onProgressUpdate(values);
            }
            @Override
            protected Integer doInBackground(String... params) {
                //加载数据方法
                return createPdf(imageFolderPath);
            }
            @Override
            protected void onPostExecute(Integer result) {
                loadingDialog.stopDialog();
                if(result==1){
                    Toast.makeText(getApplicationContext(), "生成pdf完成", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "pdf失败", Toast.LENGTH_SHORT).show();
                }

                super.onPostExecute(result);
            }

        };
        getTypeAsyncTask.execute();
    }
    private int createPdf(String[] imageFolderPath) {
        String patch = SDCardHelper.getSDCardBaseDir()+pdfpatch;
        File app_Dir = new File(patch);
        if (!app_Dir.exists()) {
            app_Dir.mkdirs();
        }

        String name = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        String pdfPath = patch+"text"+".pdf";
        try {
            File appDir = new File(pdfPath);
            if (!appDir.exists()) {
                appDir.createNewFile();
            }else{
                appDir.delete();
                File appDir2 = new File(pdfPath);
                appDir2.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建一个文档对象
        Document doc = new Document();
        try {
            //定义输出文件的位置
            PdfWriter.getInstance(doc, new FileOutputStream(pdfPath));
            //开启文档
            doc.open();
            //设定字体 为的是支持中文
            //BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            // Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
            // 循环获取图片文件夹内的图片
            for (String iname: imageFolderPath) {
                File fileimage = new File(iname);
                if (fileimage.exists()) {
                    String nPath = getFilePath(this, iname);
                    Image image = Image.getInstance(nPath);
                    float percent = getPercent(image);
                    image.setAlignment(Image.MIDDLE);
                    image.scalePercent(percent);
                    doc.add(image);
                    File file2 = new File(nPath);
                    if (file2.exists()) {
                        file2.delete();
                    }
                }
            }
            // 关闭文档
            doc.close();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 按照宽度压缩
     * @param
     */
    private Integer getPercent(Image image) {
        float height = image.getHeight();
        float width = image.getWidth();
        Float p2=530/width*100;
        Integer p=Math.round(p2);
        return p;
    }

    /**
     * 保存相册图片到新文件夹下
     *
     * @param path
     * @return
     */
    public String getFilePath(Activity context, String path) {
        if (path == null) {
            return null;
        }
        File temp = null;
        float scale = 1.0f;//大小压缩
        String tempPath = "text.png";//System.currentTimeMillis() + ".png";
        String dirFile = Environment.getExternalStorageDirectory() + pdfpatch;
        File dir = new File(dirFile);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Display display = context.getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int w0 =outMetrics.widthPixels;
        int h0=outMetrics.heightPixels;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            int w = options.outWidth;
            int h = options.outHeight;
            if (w <= h) {
                scale = (float) w / w0;
            } else {
                scale = (float) h / h0;
            }
            options.inSampleSize = (int) scale;
            options.inJustDecodeBounds = false;
            Bitmap source = BitmapFactory.decodeFile(path, options);
            int degree = Uri2BitmapUtil.getBitmapDegree(path);
            //把图片旋转为正的方向
            Bitmap newbitmap = Uri2BitmapUtil.rotateBitmapByDegree(source, degree);
            temp = new File(dir, tempPath);
            if (!temp.exists()) {
                temp.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(temp);

            newbitmap = Tools.ratio(newbitmap,1280);
            newbitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp.getAbsolutePath();
    }


    public void shareFile(String fileName) {
        /*
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("text/plain");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "share");//添加分享内容标题
        share_intent.putExtra(Intent.EXTRA_TEXT, "share with you:"+"android");//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "share");
        startActivity(share_intent);*/

        File file = new File(fileName);
        Intent share = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = MyFileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider",file);
            share.putExtra(Intent.EXTRA_STREAM, contentUri);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }
        share.setType("application/pdf");//此处可发送多种文件
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, "分享文件"));
    }
    /*
        {".3gp",    "video/3gpp"},
        {".apk",    "application/vnd.android.package-archive"},
        {".asf",    "video/x-ms-asf"},
        {".avi",    "video/x-msvideo"},
        {".bin",    "application/octet-stream"},
        {".bmp",    "image/bmp"},
        {".c",  "text/plain"},
        {".class",  "application/octet-stream"},
        {".conf",   "text/plain"},
        {".cpp",    "text/plain"},
        {".doc",    "application/msword"},
        {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
        {".xls",    "application/vnd.ms-excel"},
        {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
        {".exe",    "application/octet-stream"},
        {".gif",    "image/gif"},
        {".gtar",   "application/x-gtar"},
        {".gz", "application/x-gzip"},
        {".h",  "text/plain"},
        {".htm",    "text/html"},
        {".html",   "text/html"},
        {".jar",    "application/java-archive"},
        {".java",   "text/plain"},
        {".jpeg",   "image/jpeg"},
        {".jpg",    "image/jpeg"},
        {".js", "application/x-javascript"},
        {".log",    "text/plain"},
        {".m3u",    "audio/x-mpegurl"},
        {".m4a",    "audio/mp4a-latm"},
        {".m4b",    "audio/mp4a-latm"},
        {".m4p",    "audio/mp4a-latm"},
        {".m4u",    "video/vnd.mpegurl"},
        {".m4v",    "video/x-m4v"},
        {".mov",    "video/quicktime"},
        {".mp2",    "audio/x-mpeg"},
        {".mp3",    "audio/x-mpeg"},
        {".mp4",    "video/mp4"},
        {".mpc",    "application/vnd.mpohun.certificate"},
        {".mpe",    "video/mpeg"},
        {".mpeg",   "video/mpeg"},
        {".mpg",    "video/mpeg"},
        {".mpg4",   "video/mp4"},
        {".mpga",   "audio/mpeg"},
        {".msg",    "application/vnd.ms-outlook"},
        {".ogg",    "audio/ogg"},
        {".pdf",    "application/pdf"},
        {".png",    "image/png"},
        {".pps",    "application/vnd.ms-powerpoint"},
        {".ppt",    "application/vnd.ms-powerpoint"},
        {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
        {".prop",   "text/plain"},
        {".rc", "text/plain"},
        {".rmvb",   "audio/x-pn-realaudio"},
        {".rtf",    "application/rtf"},
        {".sh", "text/plain"},
        {".tar",    "application/x-tar"},
        {".tgz",    "application/x-compressed"},
        {".txt",    "text/plain"},
        {".wav",    "audio/x-wav"},
        {".wma",    "audio/x-ms-wma"},
        {".wmv",    "audio/x-ms-wmv"},
        {".wps",    "application/vnd.ms-works"},
        {".xml",    "text/plain"},
        {".z",  "application/x-compress"},
        {".zip",    "application/x-zip-compressed"},
     */
}
