package com.dzkandian.mvp.video.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.video.contract.VideoPlayContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.dzkandian.storage.bean.news.NewsOrVideoShareBean;
import com.dzkandian.storage.bean.video.VideoBean;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


@ActivityScope
public class VideoPlayModel extends BaseModel implements VideoPlayContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public VideoPlayModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }


    @NonNull
    @Override
    public Observable<BaseResponse<List<VideoBean>>> getVideoList(String type, int num, String beforeId,
                                                                  String version,
                                                                  String versionCode,
                                                                  String sys_name,
                                                                  String deviceId,
                                                                  String timestamp,
                                                                  String placeKeyword,
                                                                  String supportSdk) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getVideoList(type, num, beforeId
                , version, versionCode, sys_name, deviceId, timestamp,placeKeyword,supportSdk);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> videoReward(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).videoReward(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<NewsOrVideoShareBean>> videoShare(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).videoShare(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<ResponseBody> update(String fileUrl) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).update(fileUrl);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<NewBarrageBean>> foundComment(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).foundComment(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<NewBarrageBean>> getBarrage(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getBarrage(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> videoCollection(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).videoCollection(token, requestBody);
    }

    /**
     * 点赞评论接口
     */
    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> commentThumbsUp(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).commentThumbsUp(token,requestBody);
    }
}