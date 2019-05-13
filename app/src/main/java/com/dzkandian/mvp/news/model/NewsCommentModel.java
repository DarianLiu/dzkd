package com.dzkandian.mvp.news.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.news.CommentRecordBean;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.news.contract.NewsCommentContract;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class NewsCommentModel extends BaseModel implements NewsCommentContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public NewsCommentModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<CommentRecordBean>> commentRecord(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).commentRecord(token,requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<NewBarrageBean>> foundComment(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).foundComment(token,requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> commentThumbsUp(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).commentThumbsUp(token,requestBody);
    }
}