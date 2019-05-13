package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.mine.contract.MineOrderContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.mine.MyOrderBean;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class MineOrderModel extends BaseModel implements MineOrderContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public MineOrderModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<List<MyOrderBean>>> questAllOrderList(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getWithdrawalsList(token, requestBody);
    }
}