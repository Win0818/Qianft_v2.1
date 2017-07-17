package com.qianft.m.qian.utils;

import android.content.Context;

import com.qianft.m.qian.BaseApplication;
import com.qianft.m.qian.activity.MainActivity;
import com.qianft.m.qian.common.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/17.
 *
 */

public class TokenManagerUtil {

    public  static void setToken(Context context, String token, String expireTime) {

        MySharePreData.SetData(context, Constant.TOKEN_SP_NAME, Constant.TOKEN_SP_KEY, token);
        MySharePreData.SetData(context, Constant.TOKEN_SP_NAME, Constant.EXPIRE_TIME_SP_KEY, expireTime);

    }

    public static Map<String, String> getToken(Context context) {
        Map<String, String>  tokenMap = new HashMap<String, String>();
        tokenMap.put("Token", MySharePreData.GetData(context, Constant.TOKEN_SP_NAME, Constant.TOKEN_SP_KEY));
        tokenMap.put("UDID", Installation.getsInstallationId(BaseApplication.getAppContext()));
        return tokenMap;
    }
}
