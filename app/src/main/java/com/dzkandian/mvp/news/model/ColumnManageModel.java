package com.dzkandian.mvp.news.model;

import android.app.Application;
import android.support.annotation.Nullable;

import com.dzkandian.mvp.news.contract.ColumnManageContract;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import javax.inject.Inject;


@ActivityScope
public class ColumnManageModel extends BaseModel implements ColumnManageContract.Model {
    @Nullable
    @Inject
    Gson mGson;
    @Nullable
    @Inject
    Application mApplication;

    @Inject
    public ColumnManageModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }
}