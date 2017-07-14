package com.qianft.m.mvpdemo.presenter;

import android.os.Handler;
import android.os.Looper;

import com.qianft.m.mvpdemo.model.IUser;
import com.qianft.m.mvpdemo.model.UserModel;
import com.qianft.m.mvpdemo.view.ILoginView;


/**
 * Created by Administrator on 2016/10/24.
 */

public class LoginPresenterCompl implements ILoginPresenter{
    ILoginView iLoginView;
    IUser user;
    Handler handler;

    public LoginPresenterCompl(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
        initUser();
        handler = new Handler(Looper.getMainLooper());
    }

    private  void initUser() {
        user = new UserModel("mvp", "mvp");
    }
    @Override
    public void clear() {
        iLoginView.onClearText();

    }

    @Override
    public void doLogin(String name, String passwd) {
        Boolean isLoginSuccess = true;
        final int code = user.checkUserValidity(name, passwd);
        if (code != 0) isLoginSuccess = false;
        final Boolean result = isLoginSuccess;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iLoginView.onLoginResult(result, code);
            }
        }, 3000);

    }

    @Override
    public void setProgressBarvisibility(int visibility) {
        iLoginView.onSetProgressBarVivibility(visibility);
    }
}
