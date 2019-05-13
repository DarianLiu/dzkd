package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.mine.contract.UpdatePhoneContract;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class UpdatePhoneModel extends BaseModel implements UpdatePhoneContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public UpdatePhoneModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserBean>> bindPhone(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).bindPhone(token,requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserBean>> changePhone(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).changePhone(token,requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserBean>> sendSmsCode(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).sendSmsCode(token,requestBody);
    }

    //有密码时 修改密码方式
    @NonNull
    @Override
    public Observable<BaseResponse<UserBean>> existPwdRevisePwd(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).existPwdRevisePwd(token,requestBody);
    }



}