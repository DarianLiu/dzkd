package com.dzkandian.mvp.news.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.news.NewsBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.news.contract.NewBaseContract;

import java.util.List;

import io.reactivex.Observable;


@ActivityScope
public class NewBaseModel extends BaseModel implements NewBaseContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public NewBaseModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<List<NewsBean>>> getNewsList(String type, int num, String beforeId,
                                                                String version,
                                                                String versionCode,
                                                                String sys_name,
                                                                String deviceId,
                                                                String timestamp) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getNewsList(type, num, beforeId,
                version, versionCode, sys_name, deviceId, timestamp);
    }
}