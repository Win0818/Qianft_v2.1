package com.qianft.m.qian.utils;

import android.content.Context;
import android.content.res.Resources;

import com.qianft.m.qian.R;
import com.qianft.m.qian.common.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 */

public class NoAdTool {
    public static boolean hasAd(Context context, String url) {
        Resources res = context.getResources();
        String[] adUrls = res.getStringArray(R.array.adBlockUrl);
        List<String> adUrlList = java.util.Arrays.asList(adUrls);
        //如果有联网
        if (Util.isNetWorkConnected(context)) {
            String urls =  HttpUtils.getRequest(Constant.GET_ALL_ALLOW_URL);
            if (urls != null){
                adUrlList = JsonUtils.parseAdUrlJSON2List(urls);
            }
            LogUtil.d("Wing", "urls: " + urls);
        }
        for (String adUrl : adUrlList) {
            if (url.contains(adUrl)) {
                return true;
            }
        }
        return false;
    }
}
