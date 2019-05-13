package com.dzkandian.mvp.video.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.video.contract.VideoContract;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class VideoModel extends BaseModel implements VideoContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public VideoModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    /**
     * 获取视频栏目列表
     */
    @NonNull
    @Override
    public Observable<BaseResponse<List<String>>> getVideoTitleList(String version,
                                                                    String versionCode,
                                                                    String sys_name,
                                                                    String deviceId,
                                                                    String timestamp) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getVideoTitleList(version, versionCode, sys_name, deviceId, timestamp);
//        return Observable.just(mRepositoryManager.obtainRetrofitService(BaseService.class).getVideoTitleList())
//                .flatMap(responseObservable -> mRepositoryManager.obtainCacheService(CommonCache.class)
//                        .getVideoTitles(responseObservable
//                                , new DynamicKey(Constant.CACHE_DK_VIDEO_TITLE)
//                                , new EvictDynamicKey(true))
//                        .map(new Function<BaseResponse<List<String>>, BaseResponse<List<String>>>() {
//                            @Override
//                            public BaseResponse<List<String>> apply(BaseResponse<List<String>> baseResponse) throws Exception {
//                                return baseResponse;
//                            }
//                        })
//                );
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> timeReward(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).timeReward(token, requestBody);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }
}