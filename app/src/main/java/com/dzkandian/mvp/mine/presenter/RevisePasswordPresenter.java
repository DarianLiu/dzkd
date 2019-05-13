package com.dzkandian.mvp.mine.presenter;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dzkandian.app.http.utils.RequestParamUtils;
import com.dzkandian.app.http.utils.RxUtils;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.storage.bean.UserBean;
import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.RequestBody;

import javax.inject.Inject;

import com.dzkandian.mvp.mine.contract.RevisePasswordContract;
import com.jess.arms.utils.DataHelper;


@ActivityScope
public class RevisePasswordPresenter extends BasePresenter<RevisePasswordContract.Model, RevisePasswordContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public RevisePasswordPresenter(@NonNull RevisePasswordContract.Model model, @NonNull RevisePasswordContract.View rootView) {
        super(model, rootView);
    }


    private String getToken() {
        return DataHelper.getStringSF(mApplication, Constant.SP_KEY_TOKEN);
    }


    /**
     * 修改密码接口：
     * 有密码的情况
     */
    public void existPwdRevisePwd(String oldPwd, String newPwd) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.buildExistPwdRevisePwd(mApplication, oldPwd, newPwd);
            mModel.existPwdRevisePwd(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserBean userBean) {
                            mRootView.CompleteRevisePwd();
                            mRootView.killMyself();
                        }
                    });
        }
    }


    /**
     * 修改密码接口：
     * 没有密码的情况
     */
    public void notPwdRevisePwd(String code, String newPwd) {
        if (!TextUtils.isEmpty(getToken())) {
            RequestBody requestBody = RequestParamUtils.buildNotPwdRevisePwd(mApplication, code, newPwd);
            mModel.existPwdRevisePwd(getToken(), requestBody)
                    .compose(RxUtils.applySchedulers(mRootView))
                    .compose(RxUtils.handleBaseResult(mApplication))
                    .subscribe(new ErrorHandleSubscriber<UserBean>(mErrorHandler) {
                        @Override
                        public void onNext(UserBean userBean) {
                            mRootView.CompleteRevisePwd();
                            mRootView.killMyself();
                        }
                    });
        }
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
