package com.dzkandian.mvp.task_center.model;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.http.BaseService;
import com.dzkandian.mvp.task_center.contract.TaskCenterContract;
import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.dzkandian.storage.bean.mine.BannerBean;
import com.dzkandian.storage.bean.task.SignRecordBean;
import com.dzkandian.storage.bean.task.TaskListBean;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


@ActivityScope
public class TaskCenterModel extends BaseModel implements TaskCenterContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public TaskCenterModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseResponse<SignRecordBean>> signRecord(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).signRecord(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<TaskListBean>> taskList(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).taskList(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> sign(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).sign(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<Integer>> taskFinish(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).taskFinish(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<String>> wxBinding(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).wxBinding(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<TaskListBean>> alipayBinding(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).alipayBinding(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<ResponseBody> update(String fileUrl) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).update(fileUrl);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<WeChatShareBean>> inviteShare(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).inviteShare(token, requestBody);
    }

    @NonNull
    @Override
    public Observable<BaseResponse<List<BannerBean>>> banner(String token, RequestBody requestBody) {
        return mRepositoryManager.obtainRetrofitService(BaseService.class).banner(token, requestBody);
    }

}