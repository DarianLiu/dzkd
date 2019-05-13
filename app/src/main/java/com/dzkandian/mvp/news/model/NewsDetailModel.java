package com.dzkandian.mvp.news.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.dzkandian.storage.bean.news.NewsOrVideoShareBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.news.contract.NewsDetailContract;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


@ActivityScope
public class NewsDetailModel extends BaseModel implements NewsDetailContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public NewsDetailModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<Integer>> readingReward(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).readingReward(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<NewsOrVideoShareBean>> newsShare(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).newsShare(token,requestBody);
    }

    @NonNull
    @Override
    public Observable<ResponseBody> update(String fileUrl) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).update(fileUrl);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<NewBarrageBean>> getBarrage(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getBarrage(token,requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<NewBarrageBean>> foundComment(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).foundComment(token,requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> newsCollection(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).newscollection(token,requestBody);
    }

    /**
     * 点赞评论接口
     */
    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> commentThumbsUp(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).commentThumbsUp(token,requestBody);
    }

//    @NonNull
//    @Override
//    public Observable<BaseResponse<Integer>> x5web(String id, RequestBody requestBody) {
//        return mRepositoryManager.obtainRetrofitService(BaseService.class).x5web(id, requestBody);
//    }

}