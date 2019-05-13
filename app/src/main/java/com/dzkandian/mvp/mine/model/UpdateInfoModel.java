package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserBindBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.mine.AlipayInfoBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.mine.contract.UpdateInfoContract;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class UpdateInfoModel extends BaseModel implements UpdateInfoContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public UpdateInfoModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<UserInfoBean>> updateInfo(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).updateInfo(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserBindBean>> weixinPayBind(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).weixinPayBind(token, requestBody);
    }

    @Override
    public Observable<BaseResponse<AlipayInfoBean>> aLiPayLoginParam(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).aLiPayLoginParam(token, requestBody);
    }

    @Override
    public Observable<BaseResponse<String>> aLiPayBind(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).aLiPayWalletBind(token, requestBody);
    }
}