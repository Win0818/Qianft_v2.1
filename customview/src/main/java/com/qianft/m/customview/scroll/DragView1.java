package com.qianft.m.customview.scroll;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/10/27.
 */

public class DragView1 extends View {

    private int lastX;
    private int lastY;
    public DragView1(Context context) {
        super(context);
        initView();
    }

    public DragView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public DragView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    private void initView() {
        //设置View的背景颜色，便于观察
        setBackgroundColor(Color.BLUE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Log.d("Wing", "MotionEvent.ACTION_MOVE x = " + x + " y = " + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录触摸点的坐标
                Log.d("Wing", "MotionEvent.ACTION_DOWN");
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("Wing", "MotionEvent.ACTION_MOVE:");
                //计算偏移量
                int offsetX = x - lastX;
                int offsetY = y - lastY;
                // 在当前left、top、right、bottom的基础上加上偏移量
                layout(getLeft() + offsetX, getTop() + offsetY,
                        getRight() + offsetX, getBottom() + offsetY);
                break;
        }
        return true;
    }
}
