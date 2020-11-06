package com.xh.mian.myapp.appitem.home.b

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xh.mian.myapp.R
import com.xh.mian.myapp.databinding.ImageViewBinding
import com.xh.mian.myapp.tools.other.MyGlideEngine
import com.xh.mian.myapp.tools.other.Tools
import com.xh.mian.myapp.tools.uitl.Uri2BitmapUtil
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy


class ImageActivity : AppCompatActivity() {
    private lateinit var imagemodel: ImageModel
    private lateinit var mBinding: ImageViewBinding
    private val REQUEST_OK = 100
    private val PHOTOGRAPH = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Looper.myQueue().addIdleHandler { // 在这里去处理你想延时加载的东西
            // 最后返回false，后续不用再监听了。
            initModel()
            getModel()
            setModel()
            false
        }
    }
    private fun initModel(){
        mBinding =  DataBindingUtil.setContentView(this, R.layout.image_view)
        imagemodel = ViewModelProvider(this).get(ImageModel::class.java)
        mBinding.imageModel = imagemodel//关联 布局中data ->variable
    }
    private fun getModel(){

    }
    private fun setModel(){

    }
    @SuppressLint("CheckResult")
    fun setBut(v: View){
        //takeOnCamera()

        val rxPermissions = RxPermissions(this@ImageActivity)
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe { aBoolean ->
                if (aBoolean) {
                    //Log.d("##", "accept: 获得了权限")
                    Matisse.from(this@ImageActivity)
                        .choose(MimeType.ofAll())
                        .countable(true)
                        .maxSelectable(4)
                        .capture(true)//选择照片时，是否显示拍照
                        .captureStrategy(CaptureStrategy(true, "com.xh.mian.myapp.fileprovider"))//参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                        .gridExpectedSize(this@ImageActivity.getResources().getDimensionPixelSize(R.dimen.dp100))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(MyGlideEngine())
                        .forResult(REQUEST_OK)
                } else {
                    //有一个权限未获得就会执行此方法
                    Toast.makeText(this@ImageActivity, "获取权限失败", Toast.LENGTH_SHORT).show();
                }
            }
    }
    @SuppressLint("CheckResult")
    fun setBut2(v: View){
        val rxPermissions = RxPermissions(this@ImageActivity)
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe { aBoolean ->
                if (aBoolean) {
                    //Log.d("##", "accept: 获得了权限")
                    Matisse.from(this@ImageActivity)
                        .choose(MimeType.ofAll())
                        .countable(true)
                        .maxSelectable(4)
                        .gridExpectedSize(this@ImageActivity.getResources().getDimensionPixelSize(R.dimen.dp100))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(MyGlideEngine())
                        .forResult(REQUEST_OK)
                } else {
                    //有一个权限未获得就会执行此方法
                    Toast.makeText(this@ImageActivity, "获取权限失败", Toast.LENGTH_SHORT).show();
                }
            }
    }
    fun takeOnCamera() {
        val intent = Intent()
        //如果intent指定了存储图片的路径，那么onActivityResult回调中Intent对象就会为null
        //val imgFile = File("")
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile))
        try {
            intent.action = MediaStore.ACTION_IMAGE_CAPTURE
            startActivityForResult(intent, PHOTOGRAPH)
        } catch (e: Exception) {
            try {
                intent.action = MediaStore.ACTION_IMAGE_CAPTURE_SECURE
                startActivityForResult(intent, PHOTOGRAPH)
            } catch (e1: Exception) {
                try {
                    intent.action = MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE
                    startActivityForResult(intent, PHOTOGRAPH)
                } catch (ell: Exception) {
                    Toast.makeText(this@ImageActivity, "请从相册选择", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_OK) {
            try {
                val mSelected: List<Uri> = Matisse.obtainResult(data)
                //遍历将Uri转为Bitmap
                for (i in mSelected.indices) {
                    var photoBmp: Bitmap? = null
                    photoBmp = Uri2BitmapUtil.getBitmapFormUri(this@ImageActivity, mSelected[i])
                    val degree = Uri2BitmapUtil.getBitmapDegree(Uri2BitmapUtil.getFileFromMediaUri(this@ImageActivity, mSelected[i]).absolutePath)
                    //把图片旋转为正的方向
                    val newbitmap = Uri2BitmapUtil.rotateBitmapByDegree(photoBmp, degree)
                    mBinding.imgeView.setImageBitmap(newbitmap)
                }
            } catch (e: java.lang.Exception) {
                e.message
            }
        }
        if (requestCode === PHOTOGRAPH) {
            //该方式获取到的图片是缩略图
            val bundle = data?.extras
            val bitmap = bundle!!["data"] as Bitmap?
            mBinding.imgeView.setImageBitmap(bitmap)
        }
    }

}