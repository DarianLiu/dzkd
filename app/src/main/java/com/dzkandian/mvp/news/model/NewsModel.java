package com.dzkandian.mvp.news.model;

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

import com.dzkandian.mvp.news.contract.NewsContract;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class NewsModel extends BaseModel implements NewsContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public NewsModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    /**
     * 获取资讯分类列表
     */
    @NonNull
    @Override
    public Observable<BaseResponse<List<String>>> getNewsTitleList(String version,
                                                                   String versionCode,
                                                                   String sys_name,
                                                                   String deviceId,
                                                                   String timestamp) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getNewsTitleList(version,
                versionCode, sys_name, deviceId, timestamp);
//        return Observable.just(mRepositoryManager.obtainRetrofitService(BaseService.class).getNewsTitleList())
//                .flatMap(responseObservable -> mRepositoryManager.obtainCacheService(CommonCache.class)
//                .getNewsTitles(responseObservable
//                        , new DynamicKey(Constant.CACHE_DK_NEWS_TITLE)
//                        , new EvictDynamicKey(true))
//                .map(new Function<BaseResponse<List<String>>, BaseResponse<List<String>>>() {
//                    @Override
//                    public BaseResponse<List<String>> apply(BaseResponse<List<String>> baseResponse) throws Exception {
//                        return baseResponse;
//                    }
//                })
//        );
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