package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.mine.contract.UserSetActivityContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserInfoBean;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class UserSetActivityModel extends BaseModel implements UserSetActivityContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public UserSetActivityModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<String>> uploadAvatar(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).uploadAvatar(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<UserInfoBean>> uploadInfo(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).updateInfo(token, requestBody);
    }
}