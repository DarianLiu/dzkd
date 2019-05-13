package com.dzkandian.mvp.mine.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.mine.contract.MineContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.CoinBean;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.MarqueeBean;
import com.dzkandian.storage.bean.RandomAdBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


@ActivityScope
public class MineModel extends BaseModel implements MineContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public MineModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<CoinBean>> getCoin(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getCoin(token, requestBody);
    }

    @Override
    public Observable<BaseResponse<UserInfoBean>> userInfo(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).userInfo(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<MarqueeBean>> getMarquee() {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getMarquee();
    }

    @NonNull
    @Override
    public Observable<ResponseBody> update(String fileUrl) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).update(fileUrl);
    }

    @Override
    public Observable<BaseResponse<DeviceInfoBean>> getEssentialParameter(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getEssentialParameter(token, requestBody);
    }

    /**
     * 获取DSP接口
     */
    @Override
    public Observable<BaseResponse<RandomAdBean>> getRandomAd(String version, String deviceId, String supportSdk, String placeKeyword) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).getRandomAd(version, deviceId, supportSdk, placeKeyword);
    }
}