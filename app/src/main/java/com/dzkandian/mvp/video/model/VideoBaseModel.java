package com.dzkandian.mvp.video.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.video.VideoBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.video.contract.VideoBaseContract;

import java.util.List;

import io.reactivex.Observable;


@ActivityScope
public class VideoBaseModel extends BaseModel implements VideoBaseContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public VideoBaseModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<List<VideoBean>>> getVideoList(String type, int num, String beforeId,
                                                                  String version,
                                                                  String versionCode,
                                                                  String sys_name,
                                                                  String deviceId,
                                                                  String timestamp) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getVideoList(type, num, beforeId
                , version, versionCode, sys_name, deviceId, timestamp,"","");
    }
}