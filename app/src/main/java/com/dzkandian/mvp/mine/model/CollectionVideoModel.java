package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.CollectionVideoBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.mine.contract.CollectionVideoContract;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class CollectionVideoModel extends BaseModel implements CollectionVideoContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public CollectionVideoModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<List<CollectionVideoBean>>> collectionVideoList(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).videoCollectionList(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> removeVideoCollection(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).videoCollection(token,requestBody);
    }
}