package com.dzkandian.mvp.mine.model;

import android.app.Application;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.mine.contract.NotificationContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.mine.NotificationBean;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class NotificationModel extends BaseModel implements NotificationContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public NotificationModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseResponse<List<NotificationBean>>> replyList(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).notificationList(token, requestBody);
    }
}