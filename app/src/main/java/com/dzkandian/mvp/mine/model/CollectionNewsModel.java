package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.CollectionNewsBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.mine.contract.CollectionNewsContract;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class CollectionNewsModel extends BaseModel implements CollectionNewsContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public CollectionNewsModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<List<CollectionNewsBean>>> collectionNewsList(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).newscollectionlist(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> removeNewsCollection(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).newscollection(token,requestBody);
    }
}