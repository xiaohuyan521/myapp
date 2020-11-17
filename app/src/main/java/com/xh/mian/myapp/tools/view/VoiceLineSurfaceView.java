package com.xh.mian.myapp.tools.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.xh.mian.myapp.R;

import java.util.ArrayList;
import java.util.List;

public class VoiceLineSurfaceView extends SurfaceView implements Runnable,SurfaceHolder.Callback {
    private final int LINE = 0;
    private final int RECT = 1;

    private int middleLineColor = Color.BLACK;
    private int voiceLineColor = Color.BLACK;
    private float middleLineHeight = 4;
    private Paint paint;
    private Paint paintVoicLine;/**
     * 灵敏度
     */
    private int sensibility = 4;

    private float maxVolume = 100;


    private float translateX = 0;
    private boolean isSet = false;

    /**
     * 振幅
     */
    private float amplitude = 1;
    /**
     * 音量
     */
    private float volume = 10;
    private int fineness = 1;
    private float targetVolume = 1;


    private long speedY = 50;
    private float rectWidth = 25;
    private float rectSpace = 5;
    private float rectInitHeight = 4;
    private List<Rect> rectList;

    private long lastTime = 0;
    private int lineSpeed = 90;
    private SurfaceHolder surfaceHolder;
    List<Path> paths = null;
    private boolean isWaveDrawing = false;
    private boolean isMiddleLineDrawing = true;
    private Thread thread;
    private Canvas canvas;

    public VoiceLineSurfaceView(Context context) {
        super(context);
    }

    public VoiceLineSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAtts(context, attrs);
    }

    public VoiceLineSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtts(context, attrs);
    }

    private void initAtts(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.voiceView);
        //mode = typedArray.getInt(R.styleable.voiceView_viewMode, 0);
        voiceLineColor = typedArray.getColor(R.styleable.voiceView_voiceLine, Color.BLACK);
        maxVolume = typedArray.getFloat(R.styleable.voiceView_maxVolume, 100);
        sensibility = typedArray.getInt(R.styleable.voiceView_sensibility, 4);
        middleLineColor = typedArray.getColor(R.styleable.voiceView_middleLine, Color.BLACK);
        middleLineHeight = typedArray.getDimension(R.styleable.voiceView_middleLineHeight, 4);
        lineSpeed = typedArray.getInt(R.styleable.voiceView_lineSpeed, 90);
        fineness = typedArray.getInt(R.styleable.voiceView_fineness, 1);

        paths = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            paths.add(new Path());
        }
        typedArray.recycle();
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        surfaceHolder =  getHolder();
        surfaceHolder.addCallback(this);

    }
    private void drawMiddleLine(Canvas canvas) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(middleLineColor);
            paint.setAntiAlias(true);
        }
        canvas.save();
        canvas.drawRect(0, getHeight() / 2 - middleLineHeight / 2, getWidth(), getHeight() / 2 + middleLineHeight / 2, paint);
        canvas.restore();
    }

    private void drawVoiceLine(Canvas canvas) {
        lineChange();
        if (paintVoicLine == null) {
            paintVoicLine = new Paint();
            paintVoicLine.setColor(voiceLineColor);
            paintVoicLine.setAntiAlias(true);
            paintVoicLine.setStyle(Paint.Style.STROKE);
            paintVoicLine.setStrokeWidth(2);
        }
        canvas.save();
        int moveY = getHeight() / 2;
        for (int i = 0; i < paths.size(); i++) {
            paths.get(i).reset();
            paths.get(i).moveTo(getWidth(), getHeight() / 2);
        }
        for (float i = getWidth() - 1; i >= 0; i -= fineness) {
            amplitude = 4 * volume * i / getWidth() - 4 * volume * i * i / getWidth() / getWidth();
            for (int n = 1; n <= paths.size(); n++) {
                float sin = amplitude * (float) Math.sin((i - Math.pow(1.22, n)) * Math.PI / 180 - translateX);
                paths.get(n - 1).lineTo(i, (2 * n * sin / paths.size() - 15 * sin / paths.size() + moveY));
            }
        }
        for (int n = 0; n < paths.size(); n++) {
            if (n == paths.size() - 1) {
                paintVoicLine.setAlpha(255);
            } else {
                paintVoicLine.setAlpha(n * 130 / paths.size());
            }
            if (paintVoicLine.getAlpha() > 0) {
                canvas.drawPath(paths.get(n), paintVoicLine);
            }
        }
        canvas.restore();
    }
    public void setVolume(int volume) {
        if (volume > maxVolume * sensibility / 25) {
            isSet = true;
            this.targetVolume = getHeight() * volume / 2 / maxVolume;
        }
    }

    private void lineChange() {
        if (lastTime == 0) {
            lastTime = System.currentTimeMillis();
            translateX += 1.5;
        } else {
            if (System.currentTimeMillis() - lastTime > lineSpeed) {
                lastTime = System.currentTimeMillis();
                translateX += 1.5;
            } else {
                return;
            }
        }
        if (volume < targetVolume && isSet) {
            volume += getHeight() / 30;
        } else {
            isSet = false;
            if (volume <= 10) {
                volume = 10;
            } else {
                if (volume < getHeight() / 30) {
                    volume -= getHeight() / 60;
                } else {
                    volume -= getHeight() / 30;
                }
            }
        }
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        isMiddleLineDrawing = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        isMiddleLineDrawing = false;
    }
    @Override
    public void run() {
        while(isMiddleLineDrawing) {
            canvas = surfaceHolder.lockCanvas();
            if(canvas==null){
                return;
            }
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            //if(isWaveDrawing)
            {
                drawVoiceLine(canvas);
            }
            drawMiddleLine(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void setDrawing(boolean drawing){
        isMiddleLineDrawing = true;
        isWaveDrawing = drawing;
    }


}
