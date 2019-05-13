package com.dzkandian.mvp.common.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.common.contract.ForgetPwdContract;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class ForgetPwdModel extends BaseModel implements ForgetPwdContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public ForgetPwdModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<UserBean>> sendSmsCode(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).sendSmsCode(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserBean>> forgetPassword(RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).forgetPassword(requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserBean>> revisePassword(String token,RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).revicePassword(token,requestBody);
    }
}