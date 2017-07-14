package com.qianft.m.customview.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.qianft.m.customview.R;
import com.qianft.m.customview.adapter.MyListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/18.
 */

public class SecondActivity extends Activity {

    private ListView mListView;
    private List<String> dataList;
    private MyListViewAdapter mAdapter;
    int lastVisiableItemPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initData();
        mListView = (ListView) findViewById(R.id.list_view);
         mAdapter = new MyListViewAdapter(this, dataList);
        mListView.setAdapter(mAdapter);
       for(int i = 0; i <= mListView.getChildCount(); i++) {
                View view = mListView.getChildAt(i);
       }
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Wing", "MotionEvent.ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("Wing", "MotionEvent.ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Wing", "MotionEvent.ACTION_UP");
                        break;
                }
                return false;
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        Log.d("Wing", "setOnScrollListener SCROLL_STATE_IDLE");
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        Log.d("Wing", "setOnScrollListener SCROLL_STATE_TOUCH_SCROLL");
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        Log.d("Wing", "setOnScrollListener SCROLL_STATE_FLING");
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                    Log.d("Wing", "setOnScrollListener onScroll last item!");
                }

                if (firstVisibleItem > lastVisiableItemPosition) {
                    Log.d("Wing", "setOnScrollListener onScroll down!");
                } else {
                    Log.d("Wing", "setOnScrollListener onScroll up!");

                }
                lastVisiableItemPosition = firstVisibleItem;
                Log.d("Wing", "setOnScrollListener onScroll");
            }
        });
    }

    public void AddItem(View view) {
        dataList.add("news");
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(dataList.size() - 1);

    }

    @Override
    protected void onResume() {
        super.onResume();
       // mListView.setSelection(5);
    }
    private void initData() {
        dataList = new ArrayList<String>();
        dataList.add("aaa");
        dataList.add("bbb");
        dataList.add("cccc");
        dataList.add("dddd");
        dataList.add("eeee");
        dataList.add("eeee");
        dataList.add("eeee");
        dataList.add("eeee");
        dataList.add("eeee");
        dataList.add("eeee");
        dataList.add("eeee");
        dataList.add("eeee");
    }





}
