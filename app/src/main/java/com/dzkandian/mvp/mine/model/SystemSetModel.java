package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.VersionBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.mine.contract.SystemSetContract;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


@ActivityScope
public class SystemSetModel extends BaseModel implements SystemSetContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public SystemSetModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<VersionBean>> checkUpdate(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).checkUpdate(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<ResponseBody> update(String fileUrl) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).update(fileUrl);
    }
}