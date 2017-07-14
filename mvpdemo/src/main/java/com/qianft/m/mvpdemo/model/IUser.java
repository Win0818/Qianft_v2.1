package com.qianft.m.mvpdemo.model;

/**
 * Created by Administrator on 2016/10/24.
 */

public interface IUser {

        String getName();
        String getPasswd();
        int checkUserValidity(String name, String password);
}
