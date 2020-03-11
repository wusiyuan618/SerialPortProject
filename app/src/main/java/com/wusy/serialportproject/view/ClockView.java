package com.wusy.serialportproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by swt369 on 2017/8/20.
 */

public class ClockView extends View {
    //时钟各个部件的长度
    private float mScaleLengthLong;
    private float mScaleLengthShort;
    private float mTickLengthHour;
    private float mTickLengthMinute;
    private float mTickLengthSecond;
    //view的宽和高
    private float mWidth;
    private float mHeight;
    //时钟半径
    private float mRadius;

    //绘制各部件时用的Paint
    private Paint mPaintScaleLong;
    private Paint mPaintScaleShort;
    private Paint mPaintOutline;
    private Paint mPaintNum;
    private Paint mPaintTickHour;
    private Paint mPaintTickMinute;
    private Paint mPaintTickSecond;

    private ViewTreeObserver.OnPreDrawListener onPreDrawListener;
    public ClockView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            //获取view宽高并计算各个部件的长度
            @Override
            public boolean onPreDraw() {
                mWidth = getMeasuredWidth();
                mHeight = getMeasuredHeight();
                mRadius = Math.min(mWidth,mHeight) / 2 * 0.95f;
                mScaleLengthLong = mRadius * 0.1f;
                mScaleLengthShort = mRadius * 0.05f;
                mTickLengthHour = mRadius * 0.4f;
                mTickLengthMinute = mRadius * 0.6f;
                mTickLengthSecond = mRadius * 0.8f;
                return true;
            }
        };
        getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);

        //点击显示具体时间
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                String time = String.format("当前时间：%02d:%02d:%02d",
                        calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND));
                Toast.makeText(context,time, Toast.LENGTH_SHORT).show();
            }
        });

        //初始化所有Paint对象
        initializePaints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mWidth == 0 || mHeight == 0){
            return;
        }
        if(onPreDrawListener != null){
            getViewTreeObserver().removeOnPreDrawListener(onPreDrawListener);
            onPreDrawListener = null;
        }
        //绘制时钟
        drawClock(canvas);
        //一秒后重绘
        postInvalidateDelayed(1000);
    }

    private void drawClock(Canvas canvas) {
        //保存原始状态
        canvas.save();

        //将坐标系原点移到中心，并逆时针旋转90度。完成后x轴朝上。
        canvas.translate(mWidth / 2,mHeight / 2);
        canvas.rotate(-90);

        //画外围轮廓
        canvas.drawCircle(0,0,mRadius-3*mScaleLengthLong, mPaintOutline);

        //画刻度
        for(int i = 0 ; i < 60 ; i++){
//            String num = String.valueOf(i == 0 ? 12 : i);
            if(i % 5 == 0){
                canvas.drawLine(mRadius,0,mRadius - mScaleLengthLong,0, mPaintScaleLong);
            }else {
                canvas.drawLine(mRadius,0,mRadius - mScaleLengthShort,0, mPaintScaleShort);
            }
//            canvas.drawText(num,mRadius - mScaleLengthLong - mPaintNum.measureText(num) * 2,0, mPaintNum);

            //顺时针旋转30度
            canvas.rotate(6);
        }

        Calendar calendar = Calendar.getInstance();

        //画时针
        int hour = calendar.get(Calendar.HOUR);
        canvas.save();
        canvas.rotate(hour * 30);
        canvas.drawLine(0,0,mTickLengthHour,0,mPaintTickHour);
        canvas.restore();

        //画分针
        int minute = calendar.get(Calendar.MINUTE);
        canvas.save();
        canvas.rotate(minute * 6);
        canvas.drawLine(0,0,mTickLengthMinute,0,mPaintTickMinute);
        canvas.restore();

        //画秒针
        int second = calendar.get(Calendar.SECOND);
        canvas.save();
        canvas.rotate(second * 6);
        canvas.drawLine(0,0,mTickLengthSecond,0,mPaintTickSecond);
        canvas.restore();

        //恢复原始状态
        canvas.restore();
    }

    private void initializePaints(){
        mPaintScaleLong = new Paint();
        mPaintScaleLong.setAntiAlias(true);
        mPaintScaleLong.setStrokeWidth(5);
        mPaintScaleLong.setColor(Color.WHITE);


        mPaintScaleShort = new Paint();
        mPaintScaleShort.setAntiAlias(true);
        mPaintScaleShort.setStrokeWidth(3);
        mPaintScaleShort.setColor(Color.WHITE);


        mPaintOutline = new Paint();
        mPaintOutline.setStyle(Paint.Style.FILL);
        mPaintOutline.setAntiAlias(true);
        mPaintOutline.setStrokeWidth(5);
        mPaintOutline.setColor(Color.WHITE);


        mPaintNum = new Paint();
        mPaintNum.setTextSize(30);

        mPaintTickHour = new Paint();
        mPaintTickHour.setAntiAlias(true);
        mPaintTickHour.setStrokeWidth(6);
        mPaintTickHour.setColor(Color.BLACK);


        mPaintTickMinute = new Paint();
        mPaintTickMinute.setAntiAlias(true);
        mPaintTickMinute.setStrokeWidth(4);
        mPaintTickMinute.setColor(Color.BLACK);


        mPaintTickSecond = new Paint();
        mPaintTickSecond.setAntiAlias(true);
        mPaintTickSecond.setStrokeWidth(2);
        mPaintTickSecond.setColor(Color.RED);
    }
}
