package com.qianft.m.mvpdemo.view;

/**
 * Created by Administrator on 2016/10/24.
 */

public interface ILoginView {
    public void onClearText();
    public void onLoginResult(Boolean result, int code);
    public void onSetProgressBarVivibility(int visibility);
}
