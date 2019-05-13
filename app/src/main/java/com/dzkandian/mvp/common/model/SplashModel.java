package com.dzkandian.mvp.common.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.common.contract.SplashContract;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class SplashModel extends BaseModel implements SplashContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public SplashModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<DeviceInfoBean>> uploadDeviceInfo(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).uploadDeviceInfo(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<String>> isRealization(RequestBody requestBody){
        return mRepositoryManager.obtainRetrofitService(BaseService.class).realization(requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Object>> isRealizationYouMi(RequestBody requestBody){
        return mRepositoryManager.obtainRetrofitService(BaseService.class).realizationYouMi(requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<DeviceInfoBean>> getEssentialParameter(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getEssentialParameter(token, requestBody);
    }

}