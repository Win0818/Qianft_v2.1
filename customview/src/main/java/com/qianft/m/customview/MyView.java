package com.qianft.m.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2016/10/18.
 */

public class MyView extends Button {
    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("Wing", "MyView onTouchEvent: " + event.getAction());
        return super.onTouchEvent(event);
        //return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d("Wing", "MyView dispatchTouchEvent: " + event.getAction());
        return super.dispatchTouchEvent(event);
    }

}
