package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.mine.contract.QuickCashContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.mine.CoinExchangeBean;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class QuickCashModel extends BaseModel implements QuickCashContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public QuickCashModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<CoinExchangeBean>> getCoinExchange(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getCoinExchange(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<CoinExchangeBean>> redeemNow(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).redeemNow(token, requestBody);
    }
}