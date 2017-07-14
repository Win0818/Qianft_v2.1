package com.qianft.m.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/10/18.
 */

public class ShineTextView extends TextView {

    private Paint mPaint;
    private int mViewWidth;
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private int mTranslate;

    public ShineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ShineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ShineTextView(Context context) {
        super(context);
        initView();
    }
    private void initView() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("Wing", "mViewWidth: " + mViewWidth + "::::" + " w: " + w + "  h: " + h + "  oldw: " + oldw + "  oldh:  " + oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mPaint = getPaint();
                mLinearGradient = new LinearGradient(0, 0, mViewWidth, 0, new int[]{
                        Color.BLUE, 0xffffffff, Color.RED
                }, null, Shader.TileMode.CLAMP);
            }
        mPaint.setShader(mLinearGradient);
            mGradientMatrix = new Matrix();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mGradientMatrix != null) {
            Log.d("Wing", "mTranslate:  " + mTranslate + "  mViewWidth: " + mViewWidth);
            mTranslate = mTranslate + mViewWidth/5;
            if (mTranslate > 2*mViewWidth) {
                mTranslate = -mViewWidth;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(10);
        }
    }
}
