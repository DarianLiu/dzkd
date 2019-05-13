package com.dzkandian.mvp.mine.model;

import android.app.Application;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.mine.contract.MessageContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.mine.MessageBean;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.RequestBody;


@ActivityScope
public class MessageModel extends BaseModel implements MessageContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public MessageModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseResponse<List<MessageBean>>> replyPraiseList(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).replyPraiseList(token, requestBody);
    }
}