package com.qianft.m.mvpdemo.presenter;

/**
 * Created by Administrator on 2016/10/24.
 */

public interface ILoginPresenter {
    void clear();
    void doLogin(String name, String passwd);
    void setProgressBarvisibility(int visibility);
}
