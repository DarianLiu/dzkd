package com.dzkandian.mvp.common.presenter;

import android.app.Application;

import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.mvp.common.contract.RegisterContract;
import com.dzkandian.storage.bean.UserBean;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;


@ActivityScope
public class RegisterPresenter extends BasePresenter<RegisterContract.Model, RegisterContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public RegisterPresenter(@android.support.annotation.NonNull RegisterContract.Model model, @android.support.annotation.NonNull RegisterContract.View rootView) {
        super(model, rootView);
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号码
     */
    public void senSmsCode(String phone) {
        RequestBody body = RequestParamUtils.buildSendSmsCode(mApplication, phone, "USER_REG");
        mModel.sendSmsCode("", body)
                .compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                    @Override
                    public void onNext(UserBean data) {
                        mRootView.showMessage("验证码发送成功");
                        mRootView.startCountDown(60);
                    }
                });
    }


    /**
     * 注册提交信息
     *
     * @param phone    手机号码
     * @param smsCode  短信验证码
     * @param password 密码
     */
    public void register(String phone, String smsCode, String password) {
        RequestBody body = RequestParamUtils.buildRegister(mApplication, phone, smsCode, password);
        mModel.register(body).compose(RxUtils.applySchedulers(mRootView))
                .compose(RxUtils.handleBaseResult(mApplication))
                .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                    @Override
                    public void onNext(UserBean data) {
                        mRootView.showMessage("注册成功");
                        mRootView.regSuccess();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}

