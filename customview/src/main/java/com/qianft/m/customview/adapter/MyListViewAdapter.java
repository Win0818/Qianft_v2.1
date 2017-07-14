package com.qianft.m.customview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qianft.m.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */

public class MyListViewAdapter extends BaseAdapter {


    private Context context;
    private List<String> mData;
    private LayoutInflater mInflater;
    public MyListViewAdapter(Context context, List<String> data) {
        this.context = context;
        this.mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_layout, null);
            holder.img = (ImageView) convertView.findViewById(R.id.image);
            holder.title = (TextView) convertView.findViewById(R.id.list_title);
            holder.downLoadBtn = (Button) convertView.findViewById(R.id.list_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img.setBackgroundResource(R.mipmap.ic_launcher);
        holder.title.setText(position + " : " + mData.get(position));
        holder.downLoadBtn.setOnClickListener(new ClickListener());
        return convertView;
    }
    private View addFocusView(int i) {
        ImageView iv = new ImageView(context);
        iv.setImageResource(R.mipmap.ic_launcher);
        return iv;
    }

    public class ClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.list_btn:
                    Log.d("Wing", "list_btn !");
                    break;
            }
        }
    }
    public class ViewHolder {
        public ImageView img;
        public TextView title;
        public Button downLoadBtn;
    }
}
