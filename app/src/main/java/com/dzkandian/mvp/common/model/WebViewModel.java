package com.dzkandian.mvp.common.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.dzkandian.storage.bean.mine.ApprenticeMessageBean;
import com.dzkandian.storage.bean.mine.RedPacketBean;
import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.dzkandian.mvp.common.contract.WebViewContract;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


@ActivityScope
public class WebViewModel extends BaseModel implements WebViewContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public WebViewModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    /**
     * 邀请好友拆红包列表
     */
    @NonNull
    @Override
    public Observable<BaseResponse<String>> invitationRedPacket(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).invitationRedPacket(token, requestBody);
    }

    /**
     * 发消息给徒弟，提醒徒弟提现或者赚金币
     */
    @NonNull
    @Override
    public Observable<BaseResponse<String>> messageToApprentice(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).messageToApprentice(token, requestBody);
    }

    /**
     * 拆红包
     */
    @NonNull
    @Override
    public Observable<BaseResponse<String>> openRedPacket(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).openRedPacket(token, requestBody);
    }

    /**
     * 获取分享数据
     */
    @NonNull
    @Override
    public Observable<BaseResponse<WeChatShareBean>> inviteShare(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).inviteShare(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<ResponseBody> update(String fileUrl) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).update(fileUrl);
    }

    /**
     * 用户是否关注公众号接口
     */
    @NonNull
    @Override
    public Observable<BaseResponse<String>> isFoucs(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).isfoucs(token, requestBody);
    }

    /**
     * 关注公众号绑定关系接口
     */
    @NonNull
    @Override
    public Observable<BaseResponse<String>> binding(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).binding(token, requestBody);
    }
}