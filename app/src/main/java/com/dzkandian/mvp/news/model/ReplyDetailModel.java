package com.dzkandian.mvp.news.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.news.contract.ReplyDetailContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.news.ReplyBean;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class ReplyDetailModel extends BaseModel implements ReplyDetailContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public ReplyDetailModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<List<ReplyBean>>> getReplyList(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).replyList(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Object>> foundReply(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).foundReply(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> foundPraise(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).commentThumbsUp(token, requestBody);
    }
}