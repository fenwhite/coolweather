package com.coolweather.android.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.coolweather.android.R;
import com.coolweather.android.util.DisplayUtil;

public class CircleProgressView extends View {
    private static final String TAG = "CircleProgressView";
    // 背景画笔
    private Paint bgPaint;
    // 进度条画笔
    private Paint progressPaint;
    private Paint textPaint;
    // 进度
    private Integer current = 0;
    private float progressWidth;
    private int progressColor;
    private float progressRadius;
    private String text;
    // 最大进度值
    private Integer maxCurrent = 0;
    private ValueAnimator mAnimator;

    public CircleProgressView(Context context) {
        this(context,null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        if (typedArray!=null){
            progressColor = typedArray.getInt(R.styleable.CircleProgressView_progress_color, Color.WHITE);
            current = typedArray.getInt(R.styleable.CircleProgressView_progress_val, 0);
            maxCurrent = typedArray.getInt(R.styleable.CircleProgressView_progress_max,100);
            progressWidth = typedArray.getDimension(R.styleable.CircleProgressView_progress_width, dp2px(context,4));
            text = typedArray.getString(R.styleable.CircleProgressView_info_text);
            progressRadius = typedArray.getDimension(R.styleable.CircleProgressView_progress_radius,dp2px(context,30));
            typedArray.recycle();
        }

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setStrokeWidth(progressWidth);
        bgPaint.setColor(Color.parseColor("#eaecf0"));

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.SQUARE);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(progressColor);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(getResources().getColor(R.color.forecastFront));
        textPaint.setTextSize(DisplayUtil.sp2px(getContext(),15));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int computeWidth = 200;
        switch (widthModel){
            case MeasureSpec.UNSPECIFIED:
                computeWidth = (int)((progressWidth + progressRadius)*2 + 0.5f);
                break;
            case MeasureSpec.AT_MOST:
                computeWidth = Math.max(width,(int)((progressWidth + progressRadius)*2 + 0.5f));
                break;
            case MeasureSpec.EXACTLY:
                computeWidth = width;
                break;
        }
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int computeHeight = 200;
        switch (heightModel){
            case MeasureSpec.UNSPECIFIED:
                computeHeight = (int)((progressWidth + progressRadius)*2 + 0.5f);
                break;
            case MeasureSpec.AT_MOST:
                computeHeight = Math.max(height,(int)((progressWidth + progressRadius)*2 + 0.5f));
                break;
            case MeasureSpec.EXACTLY:
                computeHeight = height;
                break;
        }
        Log.d(TAG, "onMeasure: width "+width+" height "+height+" and finally width "+computeWidth+" height "+computeHeight);
        setMeasuredDimension(computeWidth,computeHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF rectf = new RectF(progressWidth,progressWidth, progressRadius * 2 + progressWidth ,  progressRadius * 2 + progressWidth);
        canvas.drawArc(rectf,120,300,false,bgPaint);

        float sweepAngle = 300 * current / maxCurrent;
        canvas.drawArc(rectf,120,sweepAngle,false,progressPaint);

        int currentLen = current.toString().length();
        canvas.drawText(current.toString(),progressWidth+progressRadius - (textPaint.getTextSize() * currentLen / 4f),progressWidth+progressRadius+textPaint.getTextSize()*1.5f,textPaint);
        if(text!=null && !"".equals(text)){
            textPaint.setTextSize(DisplayUtil.sp2px(getContext(),20));
            canvas.drawText(text,progressWidth+progressRadius - (textPaint.getTextSize() * text.length() / 2f),progressWidth+progressRadius,textPaint);
            textPaint.setTextSize(DisplayUtil.sp2px(getContext(),15));
        }
    }

    public void startAnimProgress(final int current, int duration){
        mAnimator = ValueAnimator.ofInt(0, current);
        mAnimator.setDuration(duration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                if(animatedValue!=current){
                    setCurrent(animatedValue);
                }
            }
        });
        mAnimator.start();
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
        invalidate();
    }

    public Integer getMaxCurrent() {
        return maxCurrent;
    }

    public void setMaxCurrent(Integer maxCurrent) {
        this.maxCurrent = maxCurrent;
        invalidate();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void destroy(){
        if(mAnimator!=null){
            mAnimator.cancel();
        }
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
