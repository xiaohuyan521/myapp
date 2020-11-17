package com.xh.mian.myapp.tools.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xh.mian.myapp.R;


/**
 * Author: Dkx
 * Date: 2018-09-25 14:33
 */
public class LoadingDialog {

    Dialog mLoadingDialog;


    public LoadingDialog(Context context, String msg) {

        View view = LayoutInflater.from(context).inflate(R.layout.global_dialog_view, null);
        TextView tv = (TextView) view.findViewById(R.id.global_dialog_tv);
        if (null == msg || ("").equals(msg)) {
            msg = "加载中 . . .";
        }
        tv.setText(msg + "");

        mLoadingDialog = new Dialog(context, R.style.loading_dialog);

        //按返回键是否有效
        mLoadingDialog.setCancelable(true);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 系统级别的窗口
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        // 居中显示
        params.gravity = Gravity.CENTER;
        // 设置背景透明
        params.format = PixelFormat.TRANSPARENT;
        mLoadingDialog.setContentView(view, params);
    }

    /**
     *
     * @param context
     * @param msg
     * @param isClickable 不允许点击
     */
    public LoadingDialog(Context context, String msg,boolean isClickable) {

        View view = LayoutInflater.from(context).inflate(R.layout.global_dialog_view, null);

        TextView tv = (TextView) view.findViewById(R.id.global_dialog_tv);
        if (null == msg || ("").equals(msg)) {
            msg = "加载中 . . .";
        }
        tv.setText(msg + "");

        mLoadingDialog = new Dialog(context, R.style.loading_dialog);

        //按返回键是否有效
        mLoadingDialog.setCancelable(isClickable);

        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }


    public void showDialog(){
        if (mLoadingDialog!=null){
            mLoadingDialog.show();
        }
    }

    public void stopDialog(){
        if (null!=mLoadingDialog&&mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
            mLoadingDialog=null;
        }
    }
}
