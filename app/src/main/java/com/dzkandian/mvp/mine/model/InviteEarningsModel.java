package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.mine.InviteProfitBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.mine.contract.InviteEarningsContract;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class InviteEarningsModel extends BaseModel implements InviteEarningsContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public InviteEarningsModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<List<InviteProfitBean>>> inviteProfitList(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).inviteProfitList(token, requestBody);
    }
}