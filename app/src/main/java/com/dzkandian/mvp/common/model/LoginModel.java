package com.dzkandian.mvp.common.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.common.contract.LoginContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.UserBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class LoginModel extends BaseModel implements LoginContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public LoginModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserBean>> sendSmsCode(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).sendSmsCode(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserBean>> smsLogin(RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).smsLogin(requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserBean>> login(RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).login(requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserBean>> wxLogin(RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).wxLogin(requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<DeviceInfoBean>> uploadDeviceInfo(String token , RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).uploadDeviceInfo(token,requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<DeviceInfoBean>> getEssentialParameter(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getEssentialParameter(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserInfoBean>> userInfo(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).userInfo(token,requestBody);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }
}