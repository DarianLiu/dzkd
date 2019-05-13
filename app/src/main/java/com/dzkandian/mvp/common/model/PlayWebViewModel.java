package com.dzkandian.mvp.common.model;

import android.app.Application;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.common.contract.PlayWebViewContract;



@ActivityScope
public class PlayWebViewModel extends BaseModel implements PlayWebViewContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public PlayWebViewModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }


//    @NonNull
//    @Override
//    public Observable<ResponseBody> update(String fileUrl) {
//        return mRepositoryManager.obtainRetrofitService(BaseService.class).update(fileUrl);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }
}