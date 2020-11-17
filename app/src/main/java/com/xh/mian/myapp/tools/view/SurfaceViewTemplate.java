package com.xh.mian.myapp.tools.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xh.mian.myapp.tools.uitl.CUtils;

import java.util.LinkedList;
import java.util.List;

public class SurfaceViewTemplate  extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mSurfaceHolder;
    //绘图的Canvas
    private Canvas mCanvas;
    //子线程标志位
    private boolean mIsDrawing;
    /**
     * 刻度配置
     */
    private Paint smallRulerPaint = new Paint();//小刻度画笔
    private int rulerColor = 0xffb5b5b5;//刻度的颜色
    private int rulerWidthSamll = CUtils.dip2px(0.5f);//小刻度的宽度
    private int rulerHeightSamll = CUtils.dip2px(10);//小刻度的高度
    /**
     * 大刻度
     */
    private Paint largeRulerPaint = new Paint();//大刻度画笔
    private int rulerWidthBig = CUtils.dip2px(0.5f);//大刻度的宽度
    /**
     * 上下两条线
     */
    private Paint upAndDownLinePaint = new Paint();//刻度画笔
    private int upAndDownLineWidth = CUtils.dip2px(1);//上下两条线的宽度
    private int upAndDownLineColor = rulerColor;
    /**
     * 文本画笔
     */
    private TextPaint keyTickTextPaint = new TextPaint();
    private int textColor = 0xff444242;//文本颜色
    private int textSize = CUtils.dip2px(12);//文本大小
    /**
     * 中轴线画笔
     */
    private Paint centerLinePaint = new Paint();
    private int centerLineColor = 0xff6e9fff;//中轴线画笔
    private int centerLineWidth = CUtils.dip2px(1);

    //private int viewHeight = CUtils.dip2px(178);
    //除数、刻度精度
    private int divisor=1;
    private float mRightX = 0;
    private float rRightX = 0;
    private int time = 0;
    private float itemWidth = 0;
    //Android中view的每一帧的刷新时间需要控制在16ms范围内（16 = 1000/60，保证刷新频率在60hz以上，一半20以上就比较流畅，60酒特表流畅），否则会有卡顿现象。
    private int sleep = 20;//刷新率
    private float op = 1000/sleep;
    private float wave_v = 0.2f; //1秒 画5个波段

    private int moveX = 0;

    private boolean wavek = true;
    private List<Integer> WaveYs = new LinkedList<>();
    private int WaveY = 0;

    private boolean isStart = false;

    public SurfaceViewTemplate(Context context) {
        this(context, null);
    }
    public SurfaceViewTemplate(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SurfaceViewTemplate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //创建
        mIsDrawing = true;
        //开启子线程
        new Thread(this).start();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //改变
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //销毁
        mIsDrawing = false;
    }
    @Override
    public void run() {
        //子线程
        while (mIsDrawing){
            try {
                itemWidth = getWidth() / (30 / divisor);//一屏显示30秒
                drawSomething();
                Thread.sleep(sleep);//刷新率20次为1秒 1000/20
                if(isStart){
                    updata();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //绘图逻辑
    private void drawSomething() {
        try {
            //获得canvas对象
            mCanvas = mSurfaceHolder.lockCanvas();
            //绘制背景
            mCanvas.drawColor(Color.WHITE);
            //绘图
            drawUpAndDownLine(mCanvas);
            drawRuler(mCanvas);
            drawCenterLine(mCanvas);
            drawWave(mCanvas);
        }catch (Exception e){

        }finally {
            if (mCanvas != null){
                //释放canvas对象并提交画布
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
    /**
     * 数据更新
     */
    private void updata() {
        time++;

        if(time%(wave_v*op)==0 && wavek){
            WaveYs.add(WaveY);
        }

        if(time<15*op){
            mRightX += itemWidth/op;
        }else if(time<59*op){
            rRightX -= itemWidth/op;
        }else {
            mIsDrawing = false;
        }
    }
    /**
     * 画中间线
     * @param canvas
     */
    private void drawCenterLine(Canvas canvas) {
        canvas.drawLine(mRightX, upAndDownLineWidth / 2 + textSize, mRightX, getHeight() - textSize * 2 - upAndDownLineWidth / 2 + textSize, centerLinePaint);
    }

    //重置刷新
    public void setReset(){
        isStart = false;
        WaveYs.clear();
        WaveYs.clear();
        time = 0;
    }
    //开始
    public void setStart(){
        isStart = true;
    }
    //暂停
    public void setPause(){
        isStart = false;
    }
    //销毁
    public void setDestroy(){
        setReset();
        mIsDrawing = false;
    }

    /**
     * 设置波段
     * @param
     */
    public void setWavs(List<Integer> was){
        WaveYs.clear();
        WaveYs.addAll(was);
        wavek = false;
    }
    public List<Integer> getWavs(){
        return WaveYs;
    }
    public void setWave(int type){
        switch (type){
            case 1:
                WaveY=CUtils.dip2px(0);
                break;
            case 2:
                WaveY=CUtils.dip2px(2);
                break;
            case 3:
                WaveY=CUtils.dip2px(4.5f);
                break;
            case 4:
                WaveY=CUtils.dip2px(7);
                break;
            case 5:
                WaveY=CUtils.dip2px(9.5f);
                break;
        }
    }
    /**
     * 实时音频波
     * @param canvas
     */
    private void drawWave(Canvas canvas) {
        for(int i=1;i<=WaveYs.size();i++){
            canvas.drawLine((float) (itemWidth*wave_v*i)+rRightX, getHeight()/2-WaveYs.get(i-1), (float) (itemWidth*wave_v*i)+rRightX, getHeight()/2+CUtils.dip2px(5)+WaveYs.get(i-1), upAndDownLinePaint);
        }
    }
    /**
     * 画上下两条线
     *
     * @param canvas
     */
    private void drawUpAndDownLine(Canvas canvas) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        //画上下两条线
        canvas.drawLine(0, upAndDownLineWidth / 2 + textSize, viewWidth, upAndDownLineWidth / 2 + textSize, upAndDownLinePaint);
        canvas.drawLine(0, viewHeight - textSize * 2 - upAndDownLineWidth / 2 + textSize, viewWidth, viewHeight - textSize * 2 - upAndDownLineWidth / 2 + textSize, upAndDownLinePaint);
    }
    private void drawRuler(Canvas canvas) {
        float rightX = 0;
        for (int i = 0; i < 60; i++) {
            if (i%10==0 || i==59) {
                //画上面的大刻度
                canvas.drawLine(rightX+rRightX, textSize, rightX+rRightX, rulerHeightSamll * 2+textSize, smallRulerPaint);
                //画下面的大刻度
                //canvas.drawLine(rightX, viewHeight - textSize * 2 - upAndDownLineWidth+ textSize, rightX, viewHeight - rulerHeightSamll * 2 - textSize * 2 - upAndDownLineWidth + textSize, smallRulerPaint);
                //float timeStrWidth = keyTickTextPaint.measureText(DateUtils.getHourMinute(timeIndex));
                canvas.drawText(i+"s", rightX - textSize-upAndDownLineWidth+rRightX, textSize-upAndDownLineWidth, keyTickTextPaint);
                rightX += itemWidth;
            } else if (i % divisor == 0) {
                //画上面的小刻度
                canvas.drawLine(rightX+rRightX, textSize, rightX+rRightX, rulerHeightSamll+textSize, largeRulerPaint);
                //画下面的小刻度
                //canvas.drawLine(rightX, viewHeight - textSize * 2 - upAndDownLineWidth + textSize, rightX, viewHeight - rulerHeightSamll - textSize * 2 - upAndDownLineWidth + textSize, largeRulerPaint);
                rightX += itemWidth;
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                moveX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //rRightX =  - (moveX - (int) event.getX());
                //smallRulerPaint.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     * 初始化View
     */
    private void initView(){
        mRightX = 0; rRightX = 0;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        initPaint();
    }
    /**
     * 初始化画笔
     */
    private void initPaint() {
        smallRulerPaint.setAntiAlias(true);
        smallRulerPaint.setColor(rulerColor);
        smallRulerPaint.setStrokeWidth(rulerWidthSamll);

        largeRulerPaint.setAntiAlias(true);
        largeRulerPaint.setColor(rulerColor);
        largeRulerPaint.setStrokeWidth(rulerWidthBig);

        keyTickTextPaint.setAntiAlias(true);
        keyTickTextPaint.setColor(textColor);
        keyTickTextPaint.setTextSize(textSize);

        centerLinePaint.setAntiAlias(true);
        centerLinePaint.setStrokeWidth(centerLineWidth);
        centerLinePaint.setColor(centerLineColor);

        upAndDownLinePaint.setAntiAlias(true);
        upAndDownLinePaint.setColor(upAndDownLineColor);
        upAndDownLinePaint.setStrokeWidth(upAndDownLineWidth);

    }
}
