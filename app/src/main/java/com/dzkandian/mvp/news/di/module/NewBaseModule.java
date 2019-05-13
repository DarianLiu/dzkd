package com.dzkandian.mvp.news.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.news.contract.NewBaseContract;
import com.dzkandian.mvp.news.model.NewBaseModel;


@Module
public class NewBaseModule {
    private NewBaseContract.View view;

    /**
     * 构建TestNewBaseModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public NewBaseModule(NewBaseContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    NewBaseContract.View provideTestNewBaseView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    NewBaseContract.Model provideTestNewBaseModel(NewBaseModel model) {
        return model;
    }
}