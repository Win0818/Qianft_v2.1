package com.qianft.m.customview.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.qianft.m.customview.R;
import com.qianft.m.customview.TopBar;
import com.qianft.m.customview.database.DBHelper;
import com.qianft.m.customview.util.MySharePreData;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        TopBar mTopBar = (TopBar) findViewById(R.id.topbar);
        mTopBar.setOnTopbarClickListener(new TopBar.topbarClickListener() {
            @Override
            public void leftClick() {
                Toast.makeText(MainActivity.this, "left click", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, ScrollHideListView.class));
            }

            @Override
            public void rightClick() {
                Toast.makeText(MainActivity.this, "right click", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
        MySharePreData.SetData(this, "qian", "me", "123456");
        operater();
    }

    private void network() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
    }
    private void operater() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        ContentValues values = new ContentValues();
        values.put(DBHelper.DownLoads.APP_ID, 1);
        values.put(DBHelper.DownLoads.DOWNLOAD_ID, "");
        values.put(DBHelper.DownLoads.TITLE, "qian");
        values.put(DBHelper.DownLoads.ICON,"qqq");
        values.put(DBHelper.DownLoads.STATUS, 1);
        values.put(DBHelper.DownLoads.URL, "qqq");
        values.put(DBHelper.DownLoads.PACKAGE_NAME, "qqq");
        values.put(DBHelper.DownLoads.MAIN_TYPE_NAME, "");
        values.put(DBHelper.DownLoads.PARENT_TYPE_NAME, "");
        values.put(DBHelper.DownLoads.APP_EN_NAME, "");
        values.put(DBHelper.DownLoads.AGE_TYPE, "");

        dbHelper.insertValues(DBHelper.DOWNLOADS_TABLE_NAME,
                values);

    }

    public void DragViewTest(View view) {
       startActivity(new Intent(MainActivity.this, DragViewTest.class));
    }
}
