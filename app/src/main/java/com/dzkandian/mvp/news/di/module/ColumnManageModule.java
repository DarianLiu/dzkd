package com.dzkandian.mvp.news.di.module;

import com.dzkandian.mvp.news.contract.ColumnManageContract;
import com.dzkandian.mvp.news.model.ColumnManageModel;
import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;


@Module
public class ColumnManageModule {
    private ColumnManageContract.View view;

    /**
     * 构建ColumnManageTwoModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ColumnManageModule(ColumnManageContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ColumnManageContract.View provideColumnManageTwoView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ColumnManageContract.Model provideColumnManageTwoModel(ColumnManageModel model) {
        return model;
    }
}