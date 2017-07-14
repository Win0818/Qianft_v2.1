package com.qianft.m.mvpdemo.model;

/**
 * Created by Administrator on 2016/10/24.
 */

public class UserModel implements IUser{

    private String name;
    private String password;

    public UserModel(String name, String password) {
        this.name = name;
        this.password = password;

    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPasswd() {
        return password;
    }

    @Override
    public int checkUserValidity(String name, String password) {
        if (name==null||password==null||!name.equals(getName())||!password.equals(getPasswd())){
            return -1;
        }
        return 0;
    }
}
