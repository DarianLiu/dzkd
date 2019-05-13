package com.dzkandian.mvp.mine.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.dzkandian.mvp.mine.contract.SystemSetContract;
import com.dzkandian.mvp.mine.model.SystemSetModel;


@Module
public class SystemSetModule {
    private SystemSetContract.View view;

    /**
     * 构建SystemSetModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public SystemSetModule(SystemSetContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    SystemSetContract.View provideSystemSetView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    SystemSetContract.Model provideSystemSetModel(SystemSetModel model) {
        return model;
    }
}