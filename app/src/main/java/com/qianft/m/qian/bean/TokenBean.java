package com.qianft.m.qian.bean;

/**
 * Created by Administrator on 2017/7/11.
 */

public class TokenBean {

    private String token;  //token
    private String tokenTime;   //token失效时间

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenTime() {
        return tokenTime;
    }

    public void setTokenTime(String tokenTime) {
        this.tokenTime = tokenTime;
    }
    // 定义一个私有构造方法
    private TokenBean() {

    }
    private static volatile TokenBean instance;

    public static TokenBean getTokenIstance() {
        if (instance == null) {
            synchronized (TokenBean.class) {
                if (instance == null) {
                    instance = new TokenBean();
                }
            }
        }
        return instance;
    }

    public void clearToken() {
        token = "";
        tokenTime = "";
    }
}
