package com.qianft.m.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/10/18.
 */

public class MyViewGroupA extends LinearLayout {
    public MyViewGroupA(Context context) {
        super(context);
    }

    public MyViewGroupA(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyViewGroupA(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("Wing", "MyViewGroupA dispatchTouchEvent: " + ev.getAction());
       return super.dispatchTouchEvent(ev);
       // return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("Wing", "MyViewGroupA onInterceptTouchEvent: " + ev.getAction());
        return super.onInterceptTouchEvent(ev);
        //return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("Wing", "MyViewGroupA onTouchEvent: " + event.getAction());
        return super.onTouchEvent(event);
    }
}
