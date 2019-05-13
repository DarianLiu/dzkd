package com.dzkandian.mvp.video.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.video.contract.ShortVideoPlayContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.RandomAdBean;
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
public class ShortVideoPlayModel extends BaseModel implements ShortVideoPlayContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public ShortVideoPlayModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseResponse<List<VideoBean>>> getVideoList(String type, int num, String beforeId,
                                                                  String version,
                                                                  String versionCode,
                                                                  String sys_name,
                                                                  String deviceId,
                                                                  String timestamp) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getVideoList(type, num, beforeId,
               version, versionCode, sys_name, deviceId, timestamp,"","");
    }

    @Override
    public Observable<BaseResponse<Integer>> videoReward(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).videoReward(token, requestBody);
    }

    @Override
    public Observable<BaseResponse<NewsOrVideoShareBean>> videoShare(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).videoShare(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<ResponseBody> update(String fileUrl) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).update(fileUrl);
    }

    @Override
    public Observable<BaseResponse<NewBarrageBean>> getBarrage(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getBarrage(token, requestBody);
    }

    @Override
    public Observable<BaseResponse<NewBarrageBean>> foundComment(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).foundComment(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> shortCollection(String token, RequestBody requestBody) {
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

    /**
     * 获取DSP接口
     */
    @Override
    public Observable<BaseResponse<RandomAdBean>> getRandomAd(String version, String deviceId, String supportSdk, String placeKeyword) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getRandomAd(version, deviceId, supportSdk, placeKeyword);
    }
}